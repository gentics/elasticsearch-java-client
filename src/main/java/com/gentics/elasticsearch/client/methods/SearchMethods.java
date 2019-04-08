package com.gentics.elasticsearch.client.methods;

import static com.gentics.elasticsearch.client.ClientUtility.getObjectMapper;
import static com.gentics.elasticsearch.client.ClientUtility.join;
import static com.gentics.elasticsearch.client.ClientUtility.toArray;
import static com.gentics.elasticsearch.client.ClientUtility.toJsonString;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

/**
 * Search API related methods.
 * 
 * @param <T>
 *            Response and body type
 */
public interface SearchMethods<T> extends HTTPMethods<T> {

	default RequestBuilder<T> search(T query, List<String> indices) {
		return search(query, toArray(indices));
	}

	/**
	 * Invoke a regular search request and use the selected indices. Note that a default request line limit of 4096 bytes is applied and thus the
	 * {@link #multiSearch(Object)} method should be used for large sets of indices.
	 * 
	 * @param query
	 *            Object which provides the query
	 * @param indices
	 *            Indices to select
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default RequestBuilder<T> search(T query, String... indices) {
		String indicesStr = join(indices, ",");
		String path = indicesStr + "/_search";
		return postBuilder(path, query);
	}

	/**
	 * Invoke a multisearch request.
	 * 
	 * @param data
	 * @return
	 */
	default RequestBuilder<T> multiSearch(T... data) {
		String path = "_msearch";
		return postBuilder(path, data);
	}

	/**
	 * Invoke a scrolling request.
	 * 
	 * @param request
	 * @param indices
	 * @return
	 */
	default RequestBuilder<T> searchScroll(T request, String scrollAge, String... indices) {
		String indicesStr = join(indices, ",");
		return postBuilder(indicesStr + "/_search", request).addQueryParameter("scroll", scrollAge);
	}

	default RequestBuilder<T> scroll(String scrollAge, String scrollId) {
		ObjectMapper mapper = getObjectMapper();
		String request = toJsonString(mapper.createObjectNode()
			.put("scroll", scrollAge)
			.put("scroll_id", scrollId));
		return postBuilder("/_search/scroll", (T) request);
	}

	/**
	 * Clear the scroll or multiple scrolls using the provided object.
	 * 
	 * @param json
	 * @return
	 */
	default RequestBuilder<T> clearScroll(T json) {
		return deleteBuilder("_search/scroll", json);
	}

	/**
	 * Clear the scroll with the given id.
	 * 
	 * @param id
	 * @return
	 */
	default RequestBuilder<T> clearScroll(String id) {
		return deleteBuilder("_search/scroll/" + id);
	}
}
