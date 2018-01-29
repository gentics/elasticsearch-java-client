package com.gentics.elasticsearch.client;

import io.reactivex.Single;
import okhttp3.Request.Builder;

public class RequestBuilder<T> {

	private Builder builder;
	private ElasticsearchOkClient<T> client;

	public RequestBuilder(Builder builder, ElasticsearchOkClient<T> client) {
		this.builder = builder;
		this.client = client;
	}

	/**
	 * Executes the request in a synchronized blocking way.
	 * 
	 * @return
	 * @throws HttpErrorException
	 */
	public T sync() throws HttpErrorException {
		return client.executeSync(builder.build());
	}

	/**
	 * Returns a single which can be used to execute the request and listen to the result.
	 * 
	 * @return
	 */
	public Single<T> async() {
		return client.executeAsync(builder.build());
	}

}
