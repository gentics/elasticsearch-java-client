package com.gentics.elasticsearch.client.methods;

import com.gentics.elasticsearch.client.HttpErrorException;
import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

/**
 * Info API related methods.
 *
 * @param <T>
 *            Response and body type
 */
public interface InfoMethods<T> extends HTTPMethods<T> {

	default RequestBuilder<T> info() throws HttpErrorException {
		return getBuilder("");
	}

	default RequestBuilder<T> plugins() {
		// we need to explicitly set the accept header here otherwise elasticsearch will return a plain text table
		return getBuilder("_cat/plugins").addHttpHeader("Accept", "application/json");
	}

	default RequestBuilder<T> clusterHealth() throws HttpErrorException {
		return getBuilder("_cluster/health");
	}

}
