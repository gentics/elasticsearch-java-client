package com.gentics.elasticsearch.client;

import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Request.Builder;

public class RequestBuilder<T> {

	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	private ElasticsearchOkClient<T> client;

	private okhttp3.HttpUrl.Builder urlBuilder;

	private RequestBody body;

	private String method;

	public RequestBuilder(String method, String path, T json, ElasticsearchOkClient<T> client) {
		urlBuilder = new HttpUrl.Builder()
			.scheme(client.getScheme())
			.host(client.getHostname())
			.port(client.getPort())
			.addPathSegments(path);

		RequestBody body = null;
		if (json != null) {
			body = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
		}
		this.body = body;
		this.client = client;
		this.method = method;
	}

	private Request build() {
		Builder builder = new Request.Builder().url(urlBuilder.build());
		builder.method(method, body);
		return builder.build();
	}

	/**
	 * Executes the request in a synchronized blocking way.
	 * 
	 * @return
	 * @throws HttpErrorException
	 */
	public T sync() throws HttpErrorException {
		return client.executeSync(build());
	}

	/**
	 * Returns a single which can be used to execute the request and listen to the result.
	 * 
	 * @return
	 */
	public Single<T> async() {
		return client.executeAsync(build());
	}

	/**
	 * Add an additional query parameter.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public RequestBuilder<T> addQueryParameter(String key, String value) {
		urlBuilder.addQueryParameter(key, value);
		return this;
	}
}
