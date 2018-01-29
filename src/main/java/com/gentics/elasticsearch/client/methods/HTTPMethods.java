package com.gentics.elasticsearch.client.methods;

import com.gentics.elasticsearch.client.RequestBuilder;

public interface HTTPMethods<T> {

	static final String PUT = "PUT";
	static final String GET = "GET";
	static final String DELETE = "DELETE";
	static final String POST = "POST";

	/**
	 * Create the given request to the server and return the builder which can be used to invoke the request.
	 * 
	 * @param method
	 *            Http method
	 * @param path
	 *            Request path
	 * @param json
	 *            Body data or null if no body should be send
	 * @return Created builder
	 */
	RequestBuilder<T> actionBuilder(String method, String path, T json);

	default RequestBuilder<T> putBuilder(String path, T json) {
		return actionBuilder(PUT, path, json);
	}

	default RequestBuilder<T> deleteBuilder(String path) {
		return actionBuilder(DELETE, path, null);
	}

	default RequestBuilder<T> getBuilder(String path) {
		return actionBuilder(GET, path, null);
	}

	default RequestBuilder<T> postBuilder(String path, T json) {
		return actionBuilder(POST, path, json);
	}

}
