package com.gentics.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Collection of utility methods.
 */
public final class ClientUtility {

	/**
	 * Check if the json string contains a json array as root element
	 * @param jsonStr the json string
	 * @return true if the json string contains an array as root element.
	 */
	public static Boolean isJsonArray(String jsonStr) {
		jsonStr = jsonStr.trim();
		return jsonStr.startsWith("[") && jsonStr.endsWith("]");
	}

	/**
	 * Check if the json string contains a json array as root element and
	 * will wrap the array in a json object with "arrayData" as key.
	 * If the root element is no json array the json string will be
	 * returned unchanged.
	 *
	 * @param jsonStr the json string
	 * @return a json array string wrapped in an object or the jsonString itself
	 */
	public static String wrapJsonArrays(String jsonStr) {
		if (isJsonArray(jsonStr)) {
			return "{ \"arrayData\": " +  jsonStr + "}";
		}
		return jsonStr;
	}


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
