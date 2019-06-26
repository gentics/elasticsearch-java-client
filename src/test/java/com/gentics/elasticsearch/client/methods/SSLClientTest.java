package com.gentics.elasticsearch.client.methods;

import java.io.File;
import java.time.Duration;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.elasticsearch.client.okhttp.ElasticsearchOkClient;
import com.gentics.elasticsearch.docker.ElasticsearchContainer;

import io.vertx.core.json.JsonObject;

public class SSLClientTest {

	@ClassRule
	public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer().withSSL();

	@Test
	public void testSSL() throws Exception {
		String password = elasticsearch.generateRandomPassword();

		String certDir = new File("src/test/resources/certs").getAbsolutePath();
		ElasticsearchOkClient<JsonObject> client = new ElasticsearchOkClient.Builder<JsonObject>()
			.setLogin("elastic", password)
			.setScheme("https")
			.setConnectTimeout(Duration.ofMillis(8_000))
			.setHostname(elasticsearch.getContainerIpAddress())
			.setPort(elasticsearch.getMappedPort(9200))
			.setCertPath(certDir + "/elastic-certificates.crt.pem")
			.setCaPath(certDir + "/elastic-stack-ca.crt.pem")
			.setConverterFunction(JsonObject::new)
			.build();
		client.clusterHealth().sync();
	}

}
