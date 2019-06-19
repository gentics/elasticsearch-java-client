package com.gentics.elasticsearch.client;

import java.util.function.Function;

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
	 * Return the path to the common authority certificate.
	 * 
	 * @param path
	 */
	void setCA(String path);

	/**
	 * Return the path to the SSL/TLS key.
	 * 
	 * @param path
	 */
	void setKey(String path);

	/**
	 * Return the path to the certificate of the server.
	 * 
	 * @param path
	 */
	void setCert(String path);

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
	 * Set the login used for authentication.
	 * 
	 * @param username
	 * @param password
	 */
	void setLogin(String username, String password);

	/**
	 * Set the verify hostname flag.
	 * 
	 * @param flag
	 */
	void setVerifyHostnames(boolean flag);

	/**
	 * Check whether hostnames should be verified during SSL
	 * 
	 * @return
	 */
	boolean isVerifyHostnames();

	void setConverterFunction(Function<String, T> parser);

	void setConnectTimeoutMs(int connectTimeoutMs);

	void setReadTimeoutMs(int readTimeoutMs);

	void setWriteTimeoutMs(int writeTimeoutMs);

}
