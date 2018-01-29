package com.gentics.elasticsearch.client.methods;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;

/**
 * Search API related methods.
 * 
 * @param <T>
 *            Response and body type
 */
public interface SearchMethods<T> extends HTTPMethods<T> {

	default T query(T query, List<String> indices) throws HttpErrorException {
		return query(query, indices.toArray(new String[indices.size()]));
	}

	default T query(T query, String... indices) throws HttpErrorException {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return get(path, query);
	}

	default Single<T> queryAsync(T query, String... indices) {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return getAsync(path, query);
	}

	default T queryScroll(T request, List<String> indices) throws HttpErrorException {
		return post("_search", request);
	}

	default Single<T> queryScrollAsync(T request) {
		return postAsync("_search", request);
	}

	// TODO add scroll support https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
}
