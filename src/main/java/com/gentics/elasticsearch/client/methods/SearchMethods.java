package com.gentics.elasticsearch.client.methods;

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

	default T search(T query, String... indices) throws HttpErrorException {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return get(path, query);
	}

	default Single<T> searchAsync(T query, String... indices) {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return getAsync(path, query);
	}

	// TODO add scroll support https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
}
