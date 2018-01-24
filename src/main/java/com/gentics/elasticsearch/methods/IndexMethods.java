package com.gentics.elasticsearch.methods;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.Single;

/**
 * API methods which are used to interact with indices.
 * 
 * @param <T>
 */
public interface IndexMethods<T> extends HTTPMethods<T> {

	/**
	 * Delete the given index.
	 * 
	 * @param indexName
	 * @return
	 * @throws IOException
	 */
	default T deleteIndex(String indexName) throws IOException {
		return delete(indexName);
	}

	/**
	 * Delete the index with the given name.
	 * 
	 * @param indexName
	 * @return
	 */
	default Single<T> deleteIndexAsync(String indexName) {
		return deleteAsync(indexName);
	}

	/**
	 * Create the index with the given settings.
	 * 
	 * @param indexName
	 * @param json
	 * @return
	 * @throws IOException
	 */
	default T createIndex(String indexName, T json) throws IOException {
		return put(indexName, json);
	}

	/**
	 * Create the index with the given settings.
	 * 
	 * @param indexName
	 * @param json
	 * @return
	 */
	default Single<T> createIndexAsync(String indexName, T json) {
		return putAsync(indexName, json);
	}

	/**
	 * Read the indices. Use "_all" to read all indices.
	 * 
	 * @param indices
	 * @return
	 * @throws IOException
	 */
	default T readIndex(String... indices) throws IOException {
		String indicesStr = StringUtils.join(indices, ",");
		return get("/" + indicesStr);
	}

	/**
	 * Read the indices. Use "_all" to read all indices.
	 * 
	 * @param indices
	 * @return
	 */
	default Single<T> readIndexAsync(String... indices) {
		String indicesStr = StringUtils.join(indices, ",");
		return getAsync("/" + indicesStr);
	}

	/**
	 * Refresh the given indices. All indices will be refreshed if no value has been given.
	 * 
	 * @param indices
	 * @return
	 * @throws Exception
	 */
	default T refresh(String... indices) throws Exception {
		String indicesStr = StringUtils.join(indices, ",");
		String path = "/_refresh";
		if (indices.length > 0) {
			path = "/" + indicesStr + "/_refresh";
		}
		return get(path);
	}

	/**
	 * Refresh the given indices. All indices will be refreshed if no value has been given.
	 * 
	 * @param indices
	 * @return
	 */
	default Single<T> refreshAsync(String... indices) {
		String indicesStr = StringUtils.join(indices, ",");
		String path = "/_refresh";
		if (indices.length > 0) {
			path = "/" + indicesStr + "/_refresh";
		}
		return getAsync(path);
	}

	default T createIndexTemplate(String templateName, T json) throws IOException {
		return put("/_template/" + templateName, json);
	}

	default Single<T> createIndexTemplateAsync(String templateName, T json) {
		Objects.requireNonNull(templateName, "A template name must be provided.");
		return putAsync("/_template/" + templateName, json);
	}

	default T deleteIndexTemplate(String templateName) throws IOException {
		Objects.requireNonNull(templateName, "A template name must be provided.");
		return delete("/_template/" + templateName);
	}

	default Single<T> deleteIndexTemplateAsync(String templateName) {
		Objects.requireNonNull(templateName, "A template name must be provided.");
		return deleteAsync("/_template/" + templateName);
	}
}
