package com.gentics.elasticsearch.client.okhttp;

import java.nio.charset.Charset;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Request.Builder;

public class RequestBuilder<T> {

	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	public static final MediaType MEDIA_TYPE_NDJSON = MediaType.parse("application/x-ndjson");

	private ElasticsearchOkClient<T> client;

	private okhttp3.HttpUrl.Builder urlBuilder;

	private RequestBody body;

	private String method;

	@SuppressWarnings("unchecked")
	public RequestBuilder(String method, String path, ElasticsearchOkClient<T> client, T... json) {
		urlBuilder = new HttpUrl.Builder()
			.scheme(client.getScheme())
			.host(client.getHostname())
			.port(client.getPort())
			.addPathSegments(path);

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
			body = RequestBody.create(MEDIA_TYPE_NDJSON, builder.toString().getBytes(Charset.defaultCharset()));
		}
		this.body = body;
		this.client = client;
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	public RequestBuilder(String method, String path, ElasticsearchOkClient<T> client, String bulkData) {
		urlBuilder = new HttpUrl.Builder()
			.scheme(client.getScheme())
			.host(client.getHostname())
			.port(client.getPort())
			.addPathSegments(path);

		RequestBody body = null;
		body = RequestBody.create(MEDIA_TYPE_NDJSON, bulkData.getBytes(Charset.defaultCharset()));
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
