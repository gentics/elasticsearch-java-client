package com.gentics.elasticsearch.client;

import java.util.function.Function;

/**
 * @param <T>
 *            Response and request type
 */
public abstract class AbstractElasticsearchClient<T> implements ElasticsearchClient<T> {

	protected int port;
	protected String hostname;
	protected String scheme;

	protected Function<String, T> parser;

	public AbstractElasticsearchClient(String scheme, String hostname, int port) {
		this.scheme = scheme;
		this.port = port;
		this.hostname = hostname;
	}

	/**
	 * Set the converter which will be used transform the response body to T.
	 * 
	 * @param parser
	 *            Parser function for input strings
	 */
	public void setConverterFunction(Function<String, T> parser) {
		this.parser = parser;
	}

	public int getPort() {
		return port;
	}

	public String getScheme() {
		return scheme;
	}

	public String getHostname() {
		return hostname;
	}

}
