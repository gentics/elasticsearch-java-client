package com.gentics.elasticsearch.client.methods;

import java.util.Objects;

import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

/**
 * Document API related methods.
 *
 * @param <T>
 *            Response and body type
 */
public interface DocumentMethods<T> extends HTTPMethods<T> {

	default RequestBuilder<T> storeDocument(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		String path = indexName + "/" + id;
		if (type != null) {
			path = indexName + "/" + type + "/" + id;
		}
		return putBuilder(path, json);
	}

	default RequestBuilder<T> processBulk(String bulkData) {
		return postBuilder("_bulk", bulkData);
	}

	default RequestBuilder<T> getDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		String path = indexName + "/" + id;
		if (type != null) {
			path = indexName + "/" + type + "/" + id;
		}
		return getBuilder(path);
	}

	default RequestBuilder<T> deleteDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		String path = indexName + "/" + id;
		if (type != null) {
			path = indexName + "/" + type + "/" + id;
		}
		return deleteBuilder(path);
	}

	/**
	 * Invoke an partial update on the specified document.
	 * 
	 * @param indexName
	 *            Index to be used
	 * @param type
	 *            Index type
	 * @param id
	 *            Document Id
	 * @param doc
	 *            Document
	 * @return
	 */
	default RequestBuilder<T> updateDocument(String indexName, String type, String id, T doc) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		String path = indexName + "/" + id + "/_update";
		if (type != null) {
			path = indexName + "/" + type + "/" + id + "/_update";
		}
		return postBuilder(path, doc);
	}

	/**
	 * Read the document from the index.
	 * 
	 * @param indexName
	 *            Index to be referenced
	 * @param type
	 *            Index type
	 * @param id
	 *            Document Id
	 * @return
	 */
	default RequestBuilder<T> readDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		String path = indexName + "/" + id;
		if (type != null) {
			path = indexName + "/" + type + "/" + id;
		}
		return getBuilder(path);
	}

}
