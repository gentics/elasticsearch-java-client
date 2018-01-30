package com.gentics.elasticsearch.client.methods;

import static com.gentics.elasticsearch.client.ClientUtility.join;
import static com.gentics.elasticsearch.client.ClientUtility.toArray;

import java.util.List;

import com.gentics.elasticsearch.client.RequestBuilder;

/**
 * Search API related methods.
 * 
 * @param <T>
 *            Response and body type
 */
public interface SearchMethods<T> extends HTTPMethods<T> {

	default RequestBuilder<T> query(T query, List<String> indices) {
		return query(query, toArray(indices));
	}

	default RequestBuilder<T> query(T query, String... indices) {
		String indicesStr = join(indices, ",");
		String path = indicesStr + "/_search";
		return postBuilder(path, query);
	}

	default RequestBuilder<T> queryScroll(T request, List<String> indices) {
		return postBuilder("_search/scroll", request);
	}

	// TODO add scroll support https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
}
