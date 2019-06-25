package com.gentics.elasticsearch.client.okhttp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class TrustManagerUtil {

	private TrustManagerUtil() {
	}

	public static X509TrustManager create(String certPath, String caCertPath) {
		try {
			String cert = readFile(certPath, "cert");
			String ca = readFile(caCertPath, "ca");
			return createManager(getCertChain(cert, ca));
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Error while creating custom trust manager", e);
		}
	}

	private static InputStream getCertChain(String cert, String ca) {
		String chain = cert + ca;
		return new ByteArrayInputStream(chain.getBytes());
	}

	private static X509TrustManager createManager(InputStream certChainStream) throws GeneralSecurityException {
		char[] password = "".toCharArray();

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(certChainStream);
		if (certificates.isEmpty()) {
			throw new IllegalArgumentException("expected non-empty set of trusted certificates");
		}

		// Put the certificates a key store.
		KeyStore keyStore = KeyStoreUtil.newEmptyKeyStore(password);
		int index = 0;
		for (Certificate certificate : certificates) {
			String certificateAlias = Integer.toString(index++);
			keyStore.setCertificateEntry(certificateAlias, certificate);
		}

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
			TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);

		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
			throw new IllegalStateException("Unexpected default trust managers:"
				+ Arrays.toString(trustManagers));
		}
		return (X509TrustManager) trustManagers[0];
	}


	private static String readFile(String path, String name) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			throw new RuntimeException("Could not load " + name + " from file {" + path + "}", e);
		}
	}
}
