package com.gentics.elasticsearch.client;

import java.time.Duration;

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

	/**
	 * Return the configured protocol scheme.
	 * 
	 * @return
	 */
	String getScheme();

	/**
	 * Return the configured server hostname.
	 * 
	 * @return
	 */
	String getHostname();

	/**
	 * Return the configured server port.
	 * 
	 * @return
	 */
	int getPort();

	/**
	 * Close the client and release all resources.
	 */
	void close();

	/**
	 * Check whether a login has been provided.
	 * 
	 * @return
	 */
	boolean hasLogin();

	/**
	 * Return the username used for authentication.
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * Return the password used for authentication.
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * Check whether hostnames should be verified during SSL
	 * 
	 * @return
	 */
	boolean isVerifyHostnames();

	/**
	 * Return the configured connect timeout.
	 * 
	 * @return
	 */
	Duration getConnectTimeout();

	/**
	 * Return the configured read timeout.
	 * 
	 * @return
	 */
	Duration getReadTimeout();

	/**
	 * Return the configured write timeout.
	 * 
	 * @return
	 */
	Duration getWriteTimeout();

	/**
	 * Return the path to the common authority certificate.
	 * 
	 * @return
	 */
	String getCaPath();

	/**
	 * Return the cert path.
	 * 
	 * @return
	 */
	String getCertPath();

}
