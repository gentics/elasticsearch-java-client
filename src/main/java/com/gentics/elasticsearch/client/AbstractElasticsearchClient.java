package com.gentics.elasticsearch.client;

import java.util.function.Function;

/**
 * @param <T>
 *            Response and request type
 */
public abstract class AbstractElasticsearchClient<T> implements ElasticsearchClient<T> {

	protected final String scheme;
	protected final String hostname;
	protected final int port;

	protected final String username;
	protected final String password;

	protected final String certPath;
	protected final String caPath;

	protected final int connectTimeoutMs;
	protected final int readTimeoutMs;
	protected final int writeTimeoutMs;

	protected final boolean verifyHostnames;
	protected final Function<String, T> parser;

	/**
	 * 
	 * @param scheme
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @param certPath
	 * @param caPath
	 * @param connectTimeoutMs
	 * @param readTimeoutMs
	 * @param writeTimeoutMs
	 * @param verifyHostnames
	 *            Whether hostnames should be verified for SSL
	 * @param parser
	 *            Set the converter which will be used transform the response body to T
	 */
	protected AbstractElasticsearchClient(String scheme, String hostname, int port, String username, String password, String certPath, String caPath,
		int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs, boolean verifyHostnames, Function<String, T> parser) {
		this.scheme = scheme;
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;

		this.certPath = certPath;
		this.caPath = caPath;

		this.connectTimeoutMs = connectTimeoutMs;
		this.readTimeoutMs = readTimeoutMs;
		this.writeTimeoutMs = writeTimeoutMs;

		this.verifyHostnames = verifyHostnames;
		this.parser = parser;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean hasLogin() {
		return username != null && password != null;
	}

	@Override
	public boolean isVerifyHostnames() {
		return verifyHostnames;
	}

}
