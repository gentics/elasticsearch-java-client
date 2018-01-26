package com.gentics.elasticsearch.client;

/**
 * Exception which is also used to return non-200 error responses.
 */
public class HttpErrorException extends Exception {

	private static final long serialVersionUID = -1799524340729007029L;

	public int statusCode;

	public String body;

	public HttpErrorException(String message, int statusCode, String body) {
		super(message);
		this.statusCode = statusCode;
		this.body = body;
	}

	public HttpErrorException(String message, Exception e) {
		super(message, e);
	}

	public String getBody() {
		return body;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public <T> T getBodyObject() {
		return null;
	}

}
