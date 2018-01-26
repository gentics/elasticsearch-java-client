package com.gentics.elasticsearch.client.methods;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;

/**
 * Info API related methods.
 *
 * @param <T>
 *            Response and body type
 */
public interface InfoMethods<T> extends HTTPMethods<T> {

	default T info() throws HttpErrorException {
		return get("");
	}

	default Single<T> infoAsync() {
		return getAsync("");
	}

	default T clusterHealth() throws HttpErrorException {
		return get("_cluster/health");
	}

	default Single<T> clusterHealthAsync() {
		return getAsync("_cluster/health");
	}
}
