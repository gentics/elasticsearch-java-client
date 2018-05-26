package com.gentics.elasticsearch.client.methods;

import static com.gentics.elasticsearch.client.ClientUtility.join;

import com.gentics.elasticsearch.client.HttpErrorException;
import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

/**
 * Cluster API related methods.
 */
public interface ClusterMethods<T> extends HTTPMethods<T> {

	/**
	 * Return the nodes info for the selected nodes or for all nodes if no id was specified.
	 * 
	 * @param nodeIds
	 * @return
	 */
	default RequestBuilder<T> nodesInfo(String... nodeIds) {
		String nodesStr = join(nodeIds, ",");
		String path = "_nodes";
		if (nodeIds.length > 0) {
			path = "_nodes/" + nodesStr;
		}
		return getBuilder(path);
	}

}
