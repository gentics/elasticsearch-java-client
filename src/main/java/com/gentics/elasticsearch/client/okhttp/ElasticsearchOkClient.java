package com.gentics.elasticsearch.client.okhttp;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.gentics.elasticsearch.client.AbstractElasticsearchClient;
import com.gentics.elasticsearch.client.ClientUtility;
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

	private final OkHttpClient client;

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
	public ElasticsearchOkClient(String scheme, String hostname, int port) {
		this(scheme, hostname, port, 10_000, 10_000, 10_000);
	}

	/**
	 * Create a new client with timeouts to connect, read and write to the server in milliseconds.
	 *
	 * @param scheme
	 *            Protocol scheme
	 * @param hostname
	 *            Server hostname
	 * @param port
	 *            Server port
	 * @param connectTimeoutMs
	 * 			  The timeout to connect to the server in milliseconds
	 * @param readTimeoutMs
	 *            The timeout to receive data from the server in milliseconds
	 * @param writeTimeoutMs
	 *            The timeout to send data to the server in milliseconds
	 */
	public ElasticsearchOkClient(String scheme, String hostname, int port, int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs) {
		super(scheme, hostname, port);
		this.client = createClient(connectTimeoutMs, readTimeoutMs, writeTimeoutMs);
	}

	private OkHttpClient createClient(int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs) {
		okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();

		builder.addInterceptor(chain -> {
			chain.withConnectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS);
			chain.withReadTimeout(readTimeoutMs, TimeUnit.MILLISECONDS);
			chain.withWriteTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS);
			return chain.proceed(chain.request());
		});

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
			return parser.apply(ClientUtility.wrapJsonArrays(bodyStr));
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
						sub.onSuccess(parser.apply(ClientUtility.wrapJsonArrays(bodyStr)));
					}
				}
			});
		});
	}



}
