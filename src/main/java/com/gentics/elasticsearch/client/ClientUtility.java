package com.gentics.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Collection of utility methods.
 */
public final class ClientUtility {

	/**
	 * Convert the given list into an array.
	 * 
	 * @param list
	 * @return
	 */
	public static String[] toArray(List<String> list) {
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Join the strings of the array using the separator.
	 * 
	 * @param items
	 * @param separator
	 * @return
	 */
	public static String join(String[] items, String separator) {
		if (items.length == 0) {
			return "";
		}
		final StringBuilder buf = new StringBuilder(items.length * 16);

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				buf.append(separator);
			}
			if (items[i] != null) {
				buf.append(items[i]);
			}
		}
		return buf.toString();
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Returns the default Jackson object mapper.
	 * @return
	 */
	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * Converts an object to a JSON string using the default Jackson Object mapper.
	 * @param value
	 * @return
	 */
	public static String toJsonString(Object value) {
		try {
			return getObjectMapper().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
