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

	/**
	 * Set the converter function for the client which is used to transform the server response into T. This can for example be used to return POJO's or
	 * JsonObjects instead of strings.
	 * 
	 * @param parser
	 */
	void setConverterFunction(Function<String, T> parser);

	/**
	 * Set the client connect timeout in milliseconds.
	 * 
	 * @param connectTimeoutMs
	 */
	void setConnectTimeoutMs(int connectTimeoutMs);

	/**
	 * Set the client read timeout in milliseconds.
	 * 
	 * @param readTimeoutMs
	 */
	void setReadTimeoutMs(int readTimeoutMs);

	/**
	 * Set the client write timeout in milliseconds.
	 * 
	 * @param writeTimeoutMs
	 */
	void setWriteTimeoutMs(int writeTimeoutMs);

}
