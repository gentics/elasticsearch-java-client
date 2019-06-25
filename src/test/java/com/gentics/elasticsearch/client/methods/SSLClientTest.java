package com.gentics.elasticsearch.client.methods;

import org.junit.Test;

import com.gentics.elasticsearch.client.HttpErrorException;
import com.gentics.elasticsearch.client.okhttp.ElasticsearchOkClient;

import io.vertx.core.json.JsonObject;

public class SSLClientTest {

	@Test
	public void testSSL() throws HttpErrorException {
		String certDir = "/home/johannes2/workspace_mesh/es/certs/";
		ElasticsearchOkClient<JsonObject> client = new ElasticsearchOkClient.ElasticsearchOkClientBuilder<JsonObject>()
			.setLogin("elastic", "finger")
			.setScheme("https")
			.setHostname("localhost")
			.setPort(9200)
			.setCertPath(certDir + "elastic-certificates.crt.pem")
			//.setKeyPath(certDir + "elastic-certificates.key.pem")
			.setKeyPath(certDir + "elastic-certificates.p12")
			.setCaPath(certDir + "elastic-stack-ca.crt.pem")
			.setConverterFunction(JsonObject::new)
			.build();
		JsonObject info = client.clusterHealth().sync();
		System.out.println(info.encodePrettily());
	}

}
