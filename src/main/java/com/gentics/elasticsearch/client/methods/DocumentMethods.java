package com.gentics.elasticsearch.client.methods;

import java.util.Objects;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;

/**
 * Document API related methods.
 *
 * @param <T>
 *            Response and body type
 */
public interface DocumentMethods<T> extends HTTPMethods<T> {

	default T storeDocument(String indexName, String type, String id, T json) throws HttpErrorException {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return put(indexName + "/" + type + "/" + id, json);
	}

	default Single<T> storeDocumentAsync(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return putAsync(indexName + "/" + type + "/" + id, json);
	}

	default T storeDocumentBulk(T bulkData) throws HttpErrorException {
		return post("_bulk", bulkData);
	}

	default Single<T> storeDocumentBulkAsync(T bulkData) {
		return postAsync("_bulk", bulkData);
	}

	default T getDocument(String indexName, String type, String id) throws HttpErrorException {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return get(indexName + "/" + type + "/" + id);
	}

	default Single<T> getDocumentAsync(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return getAsync(indexName + "/" + type + "/" + id);
	}

	default T deleteDocument(String indexName, String type, String id) throws HttpErrorException {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return delete(indexName + "/" + type + "/" + id);
	}

	default Single<T> deleteDocumentAsync(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return deleteAsync(indexName + "/" + type + "/" + id);
	}

	default T updateDocument(String indexName, String type, String id, T json) throws HttpErrorException {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return put(indexName + "/" + type + "/" + id, json);
	}

	default Single<T> updateDocumentAsync(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return putAsync(indexName + "/" + type + "/" + id, json);
	}

	default T readDocument(String indexName, String type, String id) throws HttpErrorException {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return get(indexName + "/" + type + "/" + id);
	}

	default Single<T> readDocumentAsync(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The index type must be specified.", type);
		Objects.requireNonNull("The document id must be specified.", id);
		return getAsync(indexName + "/" + type + "/" + id);
	}
}
