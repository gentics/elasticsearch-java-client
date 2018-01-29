package com.gentics.elasticsearch.client.methods;

import static com.gentics.elasticsearch.client.ClientUtility.join;

import java.util.Objects;

import com.gentics.elasticsearch.client.RequestBuilder;

/**
 * API methods which are used to interact with indices.
 * 
 * @param <T>
 *            Response and Body type
 */
public interface IndexMethods<T> extends HTTPMethods<T> {

	/**
	 * Delete the indices with the given names.
	 * 
	 * @param indexNames
	 * @return
	 */
	default RequestBuilder<T> deleteIndex(String... indexNames) {
		String indicesStr = join(indexNames, ",");
		return deleteBuilder(indicesStr);
	}

	/**
	 * Create the index with the given settings.
	 * 
	 * @param indexName
	 * @param json
	 * @return
	 */
	default RequestBuilder<T> createIndex(String indexName, T json) {
		return putBuilder(indexName, json);
	}

	/**
	 * Read the indices. Use "_all" to read all indices.
	 * 
	 * @param indices
	 * @return
	 */
	default RequestBuilder<T> readIndex(String... indices) {
		String indicesStr = join(indices, ",");
		return getBuilder(indicesStr);
	}

	/**
	 * Refresh the given indices. All indices will be refreshed if no value has been given.
	 * 
	 * @param indices
	 * @return
	 */
	default RequestBuilder<T> refresh(String... indices) {
		String indicesStr = join(indices, ",");
		String path = "_refresh";
		if (indices.length > 0) {
			path = indicesStr + "/_refresh";
		}
		return getBuilder(path);
	}

	default RequestBuilder<T> createIndexTemplate(String templateName, T json) {
		return putBuilder("_template/" + templateName, json);
	}

	default RequestBuilder<T> deleteIndexTemplate(String templateName) {
		Objects.requireNonNull(templateName, "A template name must be provided.");
		return deleteBuilder("_template/" + templateName);
	}

}
