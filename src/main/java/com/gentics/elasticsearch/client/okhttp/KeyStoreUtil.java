package com.gentics.elasticsearch.client.okhttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public final class KeyStoreUtil {

	private KeyStoreUtil() {
	}

	public static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
		return newEmptyKeyStore(password, KeyStore.getDefaultType());
	}

	public static KeyStore newEmptyKeyStore(char[] password, String type) throws GeneralSecurityException {
		try {
			KeyStore keyStore = KeyStore.getInstance(type);
			InputStream in = null; // By convention, 'null' creates an empty key store.
			keyStore.load(in, password);
			return keyStore;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static KeyStore readPKCS12(String password, String keyPath) throws GeneralSecurityException {
		KeyStore keystore = newEmptyKeyStore(password.toCharArray(), "PKCS12");
		try (FileInputStream bis = new FileInputStream(new File(keyPath))) {
			keystore.load(bis, password.toCharArray());
		} catch (IOException e) {
			throw new RuntimeException("Error while loading client key", e);
		}
		return keystore;
	}
}
