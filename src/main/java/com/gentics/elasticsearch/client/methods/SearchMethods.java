package com.gentics.elasticsearch.client.methods;

import org.apache.commons.lang3.StringUtils;

import com.gentics.elasticsearch.client.HttpErrorException;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

/**
 * Search API related methods.
 * 
 * @param <T>
 *            Response and body type
 */
public interface SearchMethods<T> extends HTTPMethods<T> {

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

	default T queryScroll(T request) throws HttpErrorException {
		return post("_search", request);
	}

	default Single<T> queryScrollAsync(T request) {
		return postAsync("_search", request);
	}

	// TODO add scroll support https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
}
