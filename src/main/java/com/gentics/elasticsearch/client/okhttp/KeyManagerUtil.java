package com.gentics.elasticsearch.client.okhttp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public final class KeyManagerUtil {

	private KeyManagerUtil() {
	}

	public static X509KeyManager create(String keyPath) throws GeneralSecurityException {
		char[] password = "".toCharArray();

		KeyStore clientKeyStore = KeyStoreUtil.newEmptyKeyStore(password, "PKCS12");
		try (FileInputStream bis = new FileInputStream(new File(keyPath))) {
			clientKeyStore.load(bis, password);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading client key", e);
		}

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
			KeyManagerFactory.getDefaultAlgorithm());

		keyManagerFactory.init(clientKeyStore, "".toCharArray());
		return null;

	}

	public static byte[] convertPEMToPKCS12(final String keyFile, final String cerFile,
		final String password)
		throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException,
		NoSuchProviderException {
		// Get the private key
		FileReader reader = new FileReader(keyFile);

		PEMParser pem = new PEMParser(reader);
		PEMKeyPair pemKeyPair = ((PEMKeyPair) pem.readObject());
		JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter().setProvider("BC");
		KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);

		PrivateKey key = keyPair.getPrivate();

		pem.close();
		reader.close();

		// Get the certificate
		reader = new FileReader(cerFile);
		pem = new PEMParser(reader);

		X509CertificateHolder certHolder = (X509CertificateHolder) pem.readObject();
		java.security.cert.Certificate x509Certificate = new JcaX509CertificateConverter().setProvider("BC")
			.getCertificate(certHolder);

		pem.close();
		reader.close();

		// Put them into a PKCS12 keystore and write it to a byte[]
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
		ks.load(null);
		ks.setKeyEntry("key-alias", (Key) key, password.toCharArray(),
			new java.security.cert.Certificate[] { x509Certificate });
		ks.store(bos, password.toCharArray());
		bos.close();
		return bos.toByteArray();
	}

}
