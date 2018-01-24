package com.gentics.elasticsearch.methods;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.Single;

public interface SearchMethods<T> extends HTTPMethods<T> {

	default T search(T query, String... indices) throws IOException {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return get(path, query);
	}

	default Single<T> searchAsync( T query, String... indices) {
		String indicesStr = StringUtils.join(indices, ",");
		String path = indicesStr + "/_search";
		return getAsync(path, query);
	}
	
	//TODO add scroll support  https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
}
