package com.gentics.elasticsearch.client.okhttp;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.gentics.elasticsearch.client.AbstractElasticsearchClient;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
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

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	private OkHttpClient client;

	/**
	 * Create a new client with a default timeout of 10s for all timeouts (connect, read and write).
	 * 
	 * @param scheme
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @param certPath
	 * @param caPath
	 * @param connectTimeout
	 * @param readTimeout
	 * @param writeTimeout
	 * @param verifyHostnames
	 * @param parser
	 */
	protected ElasticsearchOkClient(String scheme, String hostname, int port, String username, String password, String certPath, String caPath,
		Duration connectTimeout, Duration readTimeout, Duration writeTimeout, boolean verifyHostnames, Function<String, T> parser) {
		super(scheme, hostname, port, username, password, certPath, caPath, connectTimeout, readTimeout, writeTimeout, verifyHostnames, parser);
	}

	public void init() {
		this.client = createClient();
	}

	private OkHttpClient createClient() {
		okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(connectTimeout);
		builder.readTimeout(readTimeout);
		builder.writeTimeout(writeTimeout);

		// Check whether custom certificate chain has been set
		if (certPath != null && caPath != null) {
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

	private void configureCustomSSL(okhttp3.OkHttpClient.Builder builder) {
		try {
			// Create the trust manager which can handle and validate our custom certificate chains
			X509TrustManager trustManager = TrustManagerUtil.create(certPath, caPath);
			TrustManager[] trustManagers = new TrustManager[] { trustManager };

			// Install the custom trust manager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManagers, new java.security.SecureRandom());

			// Create an SSL socket factory with our managers
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			builder.followRedirects(true);
			builder.sslSocketFactory(sslSocketFactory, trustManager);
			if (!isVerifyHostnames()) {
				builder.hostnameVerifier((hostname, session) -> true);
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

	public static class Builder<T> {

		private String scheme = "http";
		private String hostname = "localhost";
		private int port = 9200;

		private Duration connectTimeout = Duration.ofMillis(10_000);
		private Duration readTimeout = Duration.ofMillis(10_000);
		private Duration writeTimeout = Duration.ofMillis(10_000);

		private String certPath = null;
		private String caPath = null;

		private Function<String, T> converter;

		private String username = null;
		private String password = null;
		boolean verifyHostnames = true;

		/**
		 * Verify the builder and build the client.
		 * 
		 * @return
		 */
		public ElasticsearchOkClient<T> build() {
			Objects.requireNonNull(scheme, "A protocol scheme has to be specified.");
			Objects.requireNonNull(hostname, "A hostname has to be specified.");
			Objects.requireNonNull(converter, "A converter function has to be specified.");

			ElasticsearchOkClient<T> client = new ElasticsearchOkClient<>(scheme, hostname, port,
				username, password,
				certPath, caPath,
				connectTimeout, readTimeout, writeTimeout,
				verifyHostnames, converter);
			client.init();
			return client;
		}

		/**
		 * Set the protocol scheme to be used for the client (e.g.: http, https).
		 * 
		 * @param scheme
		 * @return Fluent API
		 */
		public Builder<T> setScheme(String scheme) {
			this.scheme = scheme;
			return this;
		}

		/**
		 * Set the hostname for the client.
		 * 
		 * @param hostname
		 * @return Fluent API
		 */
		public Builder<T> setHostname(String hostname) {
			this.hostname = hostname;
			return this;
		}

		/**
		 * Set the port to connect to. (e.g. 9200).
		 * 
		 * @param port
		 * @return Fluent API
		 */
		public Builder<T> setPort(int port) {
			this.port = port;
			return this;
		}

		/**
		 * Set the login data to be used for authentication.
		 * 
		 * @param username
		 * @param password
		 * @return Fluent API
		 */
		public Builder<T> setLogin(String username, String password) {
			this.username = username;
			this.password = password;
			return this;
		}

		/**
		 * Set connection timeout.
		 * 
		 * @param connectTimeout
		 * @return Fluent API
		 */
		public Builder<T> setConnectTimeout(Duration connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		/**
		 * Set read timeout for the client.
		 * 
		 * @param readTimeout
		 * @return Fluent API
		 */
		public Builder<T> setReadTimeout(Duration readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		/**
		 * Set write timeout for the client.
		 * 
		 * @param writeTimeout
		 * @return Fluent API
		 */
		public Builder<T> setWriteTimeout(Duration writeTimeout) {
			this.writeTimeout = writeTimeout;
			return this;
		}

		/**
		 * Set the path to the server certificate which should be trusted.
		 * 
		 * @param path
		 * @return Fluent API
		 */
		public Builder<T> setCertPath(String path) {
			this.certPath = path;
			return this;
		}

		/**
		 * Set the path the Common Authority certificate which should be trusted.
		 * 
		 * @param caPath
		 * @return Fluent API
		 */
		public Builder<T> setCaPath(String caPath) {
			this.caPath = caPath;
			return this;
		}

		/**
		 * Set the converter function used to transform the server response to T.
		 * 
		 * @param converter
		 * @return Fluent API
		 */
		public Builder<T> setConverterFunction(Function<String, T> converter) {
			this.converter = converter;
			return this;
		}

		/**
		 * Control hostname verification for SSL connections.
		 * 
		 * @param verifyHostnames
		 * @return Fluent API
		 */
		public Builder<T> setVerifyHostnames(boolean verifyHostnames) {
			this.verifyHostnames = verifyHostnames;
			return this;
		}
	}

}
