package com.gentics.elasticsearch.client;

import com.gentics.elasticsearch.client.methods.ClusterMethods;
import com.gentics.elasticsearch.client.methods.DocumentMethods;
import com.gentics.elasticsearch.client.methods.IndexMethods;
import com.gentics.elasticsearch.client.methods.InfoMethods;
import com.gentics.elasticsearch.client.methods.PipelineMethods;
import com.gentics.elasticsearch.client.methods.SearchMethods;

public interface ElasticsearchClient<T> extends
	SearchMethods<T>,
	IndexMethods<T>,
	DocumentMethods<T>,
	InfoMethods<T>,
	PipelineMethods<T>,
	ClusterMethods<T> {

	void close();

}
