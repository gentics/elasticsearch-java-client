package com.gentics.elasticsearch;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import com.gentics.elasticsearch.methods.DocumentMethods;
import com.gentics.elasticsearch.methods.IndexMethods;
import com.gentics.elasticsearch.methods.SearchMethods;

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
 * Minimalistic Elasticsearch REST client.
 * 
 * @param <T>
 */
public class Client<T> implements SearchMethods<T>, IndexMethods<T>, DocumentMethods<T> {

	private final OkHttpClient client = new OkHttpClient();

	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	private int port;
	private String hostname;
	private String scheme;

	private Function<String, T> parser;

	public Client(String scheme, String hostname, int port) {
		this.scheme = scheme;
		this.port = port;
		this.hostname = hostname;
	}

	/**
	 * Set the converter which will be used transform the response body to T
	 * 
	 * @param parser
	 */
	public void setConverterFunction(Function<String, T> parser) {
		this.parser = parser;
	}

	@Override
	public T action(String method, String path, T json) throws IOException {
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
	 * @return
	 * @throws JsonSyntaxException
	 * @throws IOException
	 */
	protected T executeSync(Request request) throws IOException {
		try (Response response = client.newCall(request).execute()) {
			ResponseBody body = response.body();
			String bodyStr = "";
			if (body != null) {
				bodyStr = body.string();
			}
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response + " body {" + bodyStr + "}");
			}
			Objects.requireNonNull(parser, "No body parser was configured.");
			return parser.apply(bodyStr);
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

			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					sub.onError(e);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					try (ResponseBody responseBody = response.body()) {
						ResponseBody body = response.body();
						String bodyStr = "";
						if (body != null) {
							bodyStr = body.string();
						}
						if (!response.isSuccessful()) {
							sub.onError(new IOException("Unexpected code " + response + " body {" + bodyStr + "}"));
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
