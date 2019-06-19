package com.gentics.elasticsearch.client;

import java.util.Objects;
import java.util.function.Function;

/**
 * @param <T>
 *            Response and request type
 */
public abstract class AbstractElasticsearchClient<T> implements ElasticsearchClient<T> {

	protected int port;
	protected String hostname;
	protected String scheme;
	protected String username;
	protected String password;

	protected String certPath;
	protected String keyPath;
	protected String caPath;

	protected int connectTimeoutMs = 10_000;
	protected int readTimeoutMs = 10_000;
	protected int writeTimeoutMs = 10_000;

	protected Function<String, T> parser;
	private boolean verifyHostnames;

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
	@Override
	public void setConverterFunction(Function<String, T> parser) {
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
	public void setLogin(String username, String password) {
		Objects.requireNonNull(username, "Username must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		this.username = username;
		this.password = password;
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
	public void setCert(String path) {
		this.certPath = path;
	}

	@Override
	public void setKey(String path) {
		this.keyPath = path;
	}

	@Override
	public void setCA(String path) {
		this.caPath = path;
	}

	@Override
	public void setVerifyHostnames(boolean flag) {
		this.verifyHostnames = flag;
	}

	@Override
	public boolean isVerifyHostnames() {
		return verifyHostnames;
	}

	@Override
	public void setConnectTimeoutMs(int connectTimeoutMs) {
		this.connectTimeoutMs = connectTimeoutMs;
	}

	@Override
	public void setReadTimeoutMs(int readTimeoutMs) {
		this.readTimeoutMs = readTimeoutMs;
	}

	@Override
	public void setWriteTimeoutMs(int writeTimeoutMs) {
		this.writeTimeoutMs = writeTimeoutMs;
	}

}
