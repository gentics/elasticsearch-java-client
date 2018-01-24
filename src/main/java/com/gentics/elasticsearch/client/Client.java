package com.gentics.elasticsearch.client;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import com.gentics.elasticsearch.client.methods.DocumentMethods;
import com.gentics.elasticsearch.client.methods.IndexMethods;
import com.gentics.elasticsearch.client.methods.SearchMethods;

import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Minimal Elasticsearch REST client.
 * 
 * @param <T>
 *            Response and request type
 */
public class Client<T> implements SearchMethods<T>, IndexMethods<T>, DocumentMethods<T> {

	private final OkHttpClient client = new OkHttpClient();

	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	private int port;
	private String hostname;
	private String scheme;

	private Function<String, T> parser;

	/**
	 * Create a new client.
	 * 
	 * @param scheme
	 *            Protocol scheme
	 * @param hostname
	 *            Server hostname
	 * @param port
	 *            Server port
	 */
	public Client(String scheme, String hostname, int port) {
		this.scheme = scheme;
		this.port = port;
		this.hostname = hostname;
	}

	/**
	 * Return the used OK http client.
	 * 
	 * @return
	 */
	public OkHttpClient getOkHttpClient() {
		return client;
	}

	/**
	 * Set the converter which will be used transform the response body to T.
	 * 
	 * @param parser
	 *            Parser function for input strings
	 */
	public void setConverterFunction(Function<String, T> parser) {
		this.parser = parser;
	}

	@Override
	public T action(String method, String path, T json) throws HttpErrorException {
		HttpUrl url = new HttpUrl.Builder().scheme(scheme).host(hostname).port(port).addPathSegments(path).build();
		RequestBody body = null;
		if (json != null) {
			body = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
		}
		Request request = new Request.Builder().url(url).method(method, body).build();
		return executeSync(request);
	}

	@Override
	public Single<T> actionAsync(String method, String path, T json) {
		HttpUrl url = new HttpUrl.Builder().scheme(scheme).host(hostname).port(port).addPathSegments(path).build();
		RequestBody body = null;
		if (json != null) {
			body = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
		}
		Request request = new Request.Builder().url(url).method(method, body).build();
		return executeAsync(request);
	}

	/**
	 * Execute the request synchronously.
	 * 
	 * @param request
	 * @return Parsed response object
	 * @throws HttpErrorException
	 */
	protected T executeSync(Request request) throws HttpErrorException {
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
	protected Single<T> executeAsync(Request request) {
		return Single.create(sub -> {
			Call call = client.newCall(request);
			sub.setCancellable(call::cancel);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					sub.onError(e);
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
							sub.onError(new HttpErrorException("Request failed {" + response.message() + "}", response.code(), bodyStr));
							return;
						}
						Objects.requireNonNull(parser, "No body parser was configured.");
						sub.onSuccess(parser.apply(bodyStr));
					}
				}
			});
		});

	}
}
