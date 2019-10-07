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

	/**
	 * Store the document in the index.
	 * 
	 * @param indexName
	 * @param id
	 * @param json
	 * @return
	 */
	default RequestBuilder<T> storeDocument(String indexName, String id, T json) {
		return storeDocument(indexName, null, id, json);
	}

	/**
	 * Store the given document in the index.
	 * 
	 * @param indexName
	 * @param type
	 * @param id
	 * @param json
	 * @return
	 */
	default RequestBuilder<T> storeDocument(String indexName, String type, String id, T json) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		if (type == null) {
			type = "_doc";
		}
		String path = indexName + "/" + type + "/" + id;
		return putBuilder(path, json);
	}

	/**
	 * Process the bulk request.
	 * 
	 * @param bulkData
	 * @return
	 */
	default RequestBuilder<T> processBulk(String bulkData) {
		return postBuilder("_bulk", bulkData);
	}

	/**
	 * Load the document from the index.
	 * 
	 * @param indexName
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> getDocument(String indexName, String id) {
		return getDocument(indexName, null, id);
	}

	/**
	 * Load the document from the index.
	 * 
	 * @param indexName
	 * @param type
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> getDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		if (type == null) {
			type = "_doc";
		}
		String path = indexName + "/" + type + "/" + id;
		return getBuilder(path);
	}

	/**
	 * Delete the document from the index.
	 * 
	 * @param indexName
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> deleteDocument(String indexName, String id) {
		return deleteDocument(indexName, null, id);
	}

	/**
	 * Delete the document from the index.
	 * 
	 * @param indexName
	 * @param type
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> deleteDocument(String indexName, String type, String id) {
		Objects.requireNonNull("The indexName must be specified.", indexName);
		Objects.requireNonNull("The document id must be specified.", id);
		if (type == null) {
			type = "_doc";
		}
		String path = indexName + "/" + type + "/" + id;
		return deleteBuilder(path);
	}

	/**
	 * Invoke a partial update on the specified document.
	 * 
	 * @param indexName
	 * @param id
	 * @param doc
	 * @return
	 */
	default RequestBuilder<T> updateDocument(String indexName, String id, T doc) {
		return updateDocument(indexName, null, id, doc);
	}

	/**
	 * Invoke a partial update on the specified document.
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
		if (type == null) {
			type = "_doc";
		}
		String path = indexName + "/" + type + "/" + id + "/_update";
		return postBuilder(path, doc);
	}

	/**
	 * Read the document from the index.
	 * 
	 * @param indexName
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> readDocument(String indexName, String id) {
		return readDocument(indexName, null, id);
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
		if (type == null) {
			type = "_doc";
		}
		String path = indexName + "/" + type + "/" + id;
		return getBuilder(path);
	}

}
