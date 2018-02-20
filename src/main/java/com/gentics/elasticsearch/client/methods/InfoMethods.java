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

	default RequestBuilder<T> clusterHealth() throws HttpErrorException {
		return getBuilder("_cluster/health");
	}

}
