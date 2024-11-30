package com.gentics.elasticsearch.client.methods;

import static com.gentics.elasticsearch.client.ClientUtility.join;

import com.gentics.elasticsearch.client.HttpErrorException;
import com.gentics.elasticsearch.client.okhttp.RequestBuilder;

import java.util.Objects;

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

	/**
	 * Retrieve the current cluster settings.
	 * @return GET request to load the current cluster settings.
	 */
	default RequestBuilder<T> clusterSettings() {
        return getBuilder("_cluster/settings");
	}

	/**
	 * Update the cluster settings.
	 * @param settings The cluster settings to change.
	 * @return PUT request to update the cluster settings.
	 */
	default RequestBuilder<T> updateClusterSettings(T settings) {
		return putBuilder("_cluster/settings", settings);
	}
}
