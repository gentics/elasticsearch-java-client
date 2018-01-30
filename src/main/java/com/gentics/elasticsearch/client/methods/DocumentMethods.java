package com.gentics.elasticsearch.client.methods;

import java.util.Objects;

import com.gentics.elasticsearch.client.RequestBuilder;

/**
 * Document API related methods.
 *
 * @param <T>
 *            Response and body type
 */
public interface DocumentMethods<T> extends HTTPMethods<T> {

	default RequestBuilder<T> storeDocument(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return putBuilder(indexName + "/" + type + "/" + id, json);
	}

	default RequestBuilder<T> storeDocumentBulk(T bulkData) {
		return postBuilder("_bulk", bulkData);
	}

	default RequestBuilder<T> getDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return getBuilder(indexName + "/" + type + "/" + id);
	}

	default RequestBuilder<T> deleteDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return deleteBuilder(indexName + "/" + type + "/" + id);
	}

	/**
	 * Invoke an partial update on the specified document.
	 * 
	 * @param indexName
	 * @param type
	 * @param id
	 * @param json
	 * @return
	 */
	default RequestBuilder<T> updateDocument(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return postBuilder(indexName + "/" + type + "/" + id + "/_update", json);
	}

	default RequestBuilder<T> readDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return getBuilder(indexName + "/" + type + "/" + id);
	}

}
