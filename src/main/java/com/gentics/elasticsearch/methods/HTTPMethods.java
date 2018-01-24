package com.gentics.elasticsearch.methods;

import java.io.IOException;

import io.reactivex.Single;

public interface HTTPMethods<T> {

	static final String PUT = "PUT";
	static final String GET = "GET";
	static final String DELETE = "DELETE";
	static final String POST = "POST";

	/**
	 * Send the given request to the server (Synchronously).
	 * 
	 * @param method
	 *            Http method
	 * @param path
	 *            Request path
	 * @param json
	 *            Body data or null if no body should be send
	 * @return
	 * @throws IOException
	 */
	T action(String method, String path, T json) throws IOException;

	/**
	 * Send the given request to the server (Asynchronously).
	 * 
	 * @param method
	 *            Http method
	 * @param path
	 *            Request path
	 * @param json
	 *            Body data or null if no body should be send
	 * @return
	 */
	Single<T> actionAsync(String method, String path, T json);

	default T put(String path, T json) throws IOException {
		return action(PUT, path, json);
	}

	default Single<T> putAsync(String path, T json) {
		return actionAsync(PUT, path, json);
	}

	default T delete(String path) throws IOException {
		return action(DELETE, path, null);
	}

	default Single<T> deleteAsync(String path) {
		return actionAsync(DELETE, path, null);
	}

	default T get(String path) throws IOException {
		return action(GET, path, null);
	}

	default T get(String path, T json) throws IOException {
		return action(GET, path, json);
	}

	default Single<T> getAsync(String path) {
		return actionAsync(GET, path, null);
	}

	default Single<T> getAsync(String path, T json) {
		return actionAsync(GET, path, json);
	}

	default T post(String path, T json) throws IOException {
		return action(POST, path, json);
	}

	default Single<T> postAsync(String path, T json) {
		return actionAsync(POST, path, json);
	}

}
