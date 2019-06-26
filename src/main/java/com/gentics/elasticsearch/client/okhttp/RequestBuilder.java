package com.gentics.elasticsearch.client.okhttp;

import java.nio.charset.StandardCharsets;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

public class RequestBuilder<T> {

	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	public static final MediaType MEDIA_TYPE_NDJSON = MediaType.parse("application/x-ndjson");

	private final ElasticsearchOkClient<T> client;

	private final okhttp3.HttpUrl.Builder urlBuilder;

	private RequestBody body;

	private final String method;

	@SuppressWarnings("unchecked")
	public RequestBuilder(String method, String path, ElasticsearchOkClient<T> client, T... json) {
		this.client = client;
		this.method = method;
		this.urlBuilder = createUrlBuilder(path);

		RequestBody body = null;
		if (json != null && json.length == 1) {
			body = RequestBody.create(MEDIA_TYPE_JSON, json[0].toString());
		}
		if (json != null && json.length > 1) {
			StringBuilder builder = new StringBuilder();
			for (T element : json) {
				builder.append(element.toString());
				builder.append("\n");
			}
			body = RequestBody.create(MEDIA_TYPE_NDJSON, builder.toString().getBytes(StandardCharsets.UTF_8));
		}
		this.body = body;
	}

	public RequestBuilder(String method, String path, ElasticsearchOkClient<T> client, String bulkData) {
		this.client = client;
		this.method = method;
		this.urlBuilder = createUrlBuilder(path);
		this.body = RequestBody.create(MEDIA_TYPE_NDJSON, bulkData.getBytes(StandardCharsets.UTF_8));
	}

	private okhttp3.HttpUrl.Builder createUrlBuilder(String path) {
		return new HttpUrl.Builder()
			.scheme(client.getScheme())
			.host(client.getHostname())
			.port(client.getPort())
			.addPathSegments(path);
	}

	private Request build() {
		Builder builder = new Request.Builder().url(urlBuilder.build());
		if (client.hasLogin()) {
			builder.header("Authorization", Credentials.basic(client.getUsername(), client.getPassword()));
		}
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
