package com.gentics.elasticsearch.client.okhttp;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.gentics.elasticsearch.client.AbstractElasticsearchClient;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Minimal Elasticsearch REST client.
 * 
 * @param <T>
 *            Response and request type
 */
public class ElasticsearchOkClient<T> extends AbstractElasticsearchClient<T> {

	private OkHttpClient client;

	/**
	 * Create a new client with a default timeout of 10s for all timeouts (connect, read and write).
	 * 
	 * @param scheme
	 *            Protocol scheme
	 * @param hostname
	 *            Server hostname
	 * @param port
	 *            Server port
	 */
	protected ElasticsearchOkClient(String scheme, String hostname, int port) {
		super(scheme, hostname, port);
	}

	public void init() {
		this.client = createClient();
	}

	private OkHttpClient createClient() {
		okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();

		builder.addInterceptor(chain -> {
			chain.withConnectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS);
			chain.withReadTimeout(readTimeoutMs, TimeUnit.MILLISECONDS);
			chain.withWriteTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS);
			return chain.proceed(chain.request());
		});

		// Check whether custom certificate chain has been set
		if (certPath != null && keyPath != null && caPath != null) {
			configureCustomSSL(builder);
		}

		// Disable gzip
		builder.addInterceptor(chain -> {
			Request request = chain.request();
			Request newRequest;
			try {
				newRequest = request.newBuilder().addHeader("Accept-Encoding", "identity").build();
			} catch (Exception e) {
				e.printStackTrace();
				return chain.proceed(request);
			}
			return chain.proceed(newRequest);
		});
		return builder.build();
	}

	private void configureCustomSSL(Builder builder) {
		try {
			// Create the trust manager which can handle and validate our custom certificate chains
			X509TrustManager trustManager = TrustManagerUtil.create(certPath, keyPath, caPath);
			TrustManager[] trustManagers = new TrustManager[] { trustManager };

			X509KeyManager clientKeyManager = KeyManagerUtil.create(keyPath);
			KeyManager[] keyManagers = new KeyManager[] { clientKeyManager };

			// Install the custom trust manager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			//sslContext.init(keyManager, customTrustCerts, new java.security.SecureRandom());
			sslContext.init(keyManagers, trustManagers, null);

			// Create an SSL socket factory with our manager
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			builder.followRedirects(true);
			builder.sslSocketFactory(sslSocketFactory, trustManager);
			if (!isVerifyHostnames()) {
				builder.hostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while configuring SSL options", e);
		}

	}

	/**
	 * Return the used OK HTTP client.
	 * 
	 * @return
	 */
	public OkHttpClient getOkHttpClient() {
		return client;
	}

	@Override
	public void close() {
		// Not needed for OkClient
	}

	@Override
	@SuppressWarnings("unchecked")
	public RequestBuilder<T> actionBuilder(String method, String path, T... json) {
		return new RequestBuilder<>(method, path, this, json);
	}

	@Override
	public RequestBuilder<T> actionBuilder(String method, String path, String bulkData) {
		return new RequestBuilder<>(method, path, this, bulkData);
	}

	/**
	 * Execute the request synchronously.
	 * 
	 * @param request
	 * @return Parsed response object
	 * @throws HttpErrorException
	 */
	public T executeSync(Request request) throws HttpErrorException {
		try (Response response = client.newCall(request).execute()) {
			ResponseBody body = response.body();
			String bodyStr = "";
			if (body != null) {
				try {
					bodyStr = body.string();
				} catch (Exception e) {
					throw new HttpErrorException("Error while reading body", e);
				}
			}
			if (!response.isSuccessful()) {
				throw new HttpErrorException("Request failed {" + response.message() + "}", response.code(), bodyStr);
			}
			Objects.requireNonNull(parser, "No body parser was configured.");
			return parser.apply(bodyStr);
		} catch (IOException e1) {
			throw new HttpErrorException("Error while excuting request", e1);
		}
	}

	/**
	 * Execute the request asynchronously.
	 * 
	 * @param request
	 * @return Single which yields the response data
	 */
	public Single<T> executeAsync(Request request) {
		return Single.create(sub -> {
			Call call = client.newCall(request);
			sub.setCancellable(call::cancel);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					// Don't call the onError twice. Cancelling will trigger another error.
					if (!"Canceled".equals(e.getMessage())) {
						sub.onError(e);
					}
				}

				@Override
				public void onResponse(Call call, Response response) {
					try (ResponseBody responseBody = response.body()) {
						ResponseBody body = response.body();
						String bodyStr = "";
						if (body != null) {
							try {
								bodyStr = body.string();
							} catch (Exception e) {
								sub.onError(new HttpErrorException("Error while reading body", e));
								return;
							}
						}
						if (!response.isSuccessful()) {
							sub.onError(new HttpErrorException("Request failed", response.code(), bodyStr));
							return;
						}
						Objects.requireNonNull(parser, "No body parser was configured.");
						sub.onSuccess(parser.apply(bodyStr));
					}
				}
			});
		});
	}

	public static class ElasticsearchOkClientBuilder<T> {

		private String scheme = "http";
		private String hostname = "localhost";
		private int port = 9200;

		private int connectTimeoutMs = 10_000;
		private int readTimeoutMs = 10_000;
		private int writeTimeoutMs = 10_000;

		private String certPath;
		private String keyPath;
		private String caPath;

		private Function<String, T> converter;

		private String username;
		private String password;

		public ElasticsearchOkClient<T> build() {
			ElasticsearchOkClient<T> client = new ElasticsearchOkClient<>(scheme, hostname, port);
			if (username != null) {
				client.setLogin(username, password);
			}

			client.setConnectTimeoutMs(connectTimeoutMs);
			client.setReadTimeoutMs(readTimeoutMs);
			client.setWriteTimeoutMs(writeTimeoutMs);

			if (certPath != null) {
				client.setCert(certPath);
			}
			if (keyPath != null) {
				client.setKey(keyPath);
			}
			if (caPath != null) {
				client.setCA(caPath);
			}

			if (converter != null) {
				client.setConverterFunction(converter);
			}
			client.init();
			return client;
		}

		public ElasticsearchOkClientBuilder<T> setScheme(String scheme) {
			this.scheme = scheme;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setHostname(String hostname) {
			this.hostname = hostname;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setPort(int port) {
			this.port = port;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setLogin(String username, String password) {
			this.username = username;
			this.password = password;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setConnectTimeoutMs(int connectTimeoutMs) {
			this.connectTimeoutMs = connectTimeoutMs;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setReadTimeoutMs(int readTimeoutMs) {
			this.readTimeoutMs = readTimeoutMs;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setWriteTimeoutMs(int writeTimeoutMs) {
			this.writeTimeoutMs = writeTimeoutMs;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setCertPath(String path) {
			this.certPath = path;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setCaPath(String caPath) {
			this.caPath = caPath;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setKeyPath(String keyPath) {
			this.keyPath = keyPath;
			return this;
		}

		public ElasticsearchOkClientBuilder<T> setConverterFunction(Function<String, T> converter) {
			this.converter = converter;
			return this;
		}
	}

	public static <T> ElasticsearchOkClientBuilder<T> builder() {
		return new ElasticsearchOkClientBuilder<T>();
	}

}
