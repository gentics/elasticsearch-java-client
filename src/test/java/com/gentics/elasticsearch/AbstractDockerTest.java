package com.gentics.elasticsearch;

import org.junit.BeforeClass;
import org.junit.ClassRule;

import com.gentics.elasticsearch.client.okhttp.ElasticsearchOkClient;
import com.gentics.elasticsearch.docker.ElasticsearchContainer;

import io.vertx.core.json.JsonObject;

public class AbstractDockerTest {

	@ClassRule
	public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(true);

	public static ElasticsearchOkClient<JsonObject> client;

	@BeforeClass
	public static void setupClient() {
		client = new ElasticsearchOkClient.Builder<JsonObject>()
			.setScheme("http")
			.setHostname("localhost")
			.setPort(elasticsearch.getMappedPort(9200))
			.setConverterFunction(JsonObject::new)
			.build();
	}

}
