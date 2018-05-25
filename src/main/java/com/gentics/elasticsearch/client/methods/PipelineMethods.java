package com.gentics.elasticsearch.client.methods;

import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

public interface PipelineMethods<T> extends HTTPMethods<T> {

	/**
	 * Register the pipeline with the given name
	 * 
	 * @param name
	 *            Pipeline name
	 * @param payload
	 *            Pipeline configuration
	 * @return
	 */
	default RequestBuilder<T> registerPipeline(String name, T payload) {
		String path = "_ingest/pipeline/" + name;
		return putBuilder(path, payload);
	}

	default RequestBuilder<T> listPipelines() {
		String path = "_ingest/pipeline";
		return getBuilder(path);
	}

	/**
	 * Deregister the pipeline with the given name.
	 * 
	 * @param name
	 *            Pipeline name
	 * @return
	 */
	default RequestBuilder<T> deregisterPlugin(String name) {
		String path = "_ingest/pipeline/" + name;
		return deleteBuilder(path);
	}

}
