package com.gentics.elasticsearch.client.methods;

import java.util.Objects;

import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

public interface PipelineMethods<T> extends HTTPMethods<T> {

	/**
	 * Register the pipeline with the given name
	 * 
	 * @param name
	 *            Pipeline name
	 * @param config
	 *            Pipeline configuration
	 * @return
	 */
	default RequestBuilder<T> registerPipeline(String name, T config) {
		Objects.requireNonNull(name, "A name of the pipeline must be specified.");
		Objects.requireNonNull(config, "The pipeline config is missing.");
		String path = "_ingest/pipeline/" + name;
		return putBuilder(path, config);
	}

	/**
	 * Read a list of all registered pipelines.
	 * 
	 * @return
	 */
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
		Objects.requireNonNull(name, "A name of the pipeline must be specified.");
		String path = "_ingest/pipeline/" + name;
		return deleteBuilder(path);
	}

}
