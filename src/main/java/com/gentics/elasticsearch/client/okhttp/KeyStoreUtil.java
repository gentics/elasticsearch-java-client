package com.gentics.elasticsearch.client.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Utility class used to interact with keystore instances.
 */
public final class KeyStoreUtil {

	private KeyStoreUtil() {
	}

	public static KeyStore newEmptyKeyStore(String password) throws GeneralSecurityException {
		return newEmptyKeyStore(password, KeyStore.getDefaultType());
	}

	public static KeyStore newEmptyKeyStore(String password, String type) throws GeneralSecurityException {
		try {
			KeyStore keyStore = KeyStore.getInstance(type);
			InputStream in = null; // By convention, 'null' creates an empty key store.
			keyStore.load(in, password.toCharArray());
			return keyStore;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

}
