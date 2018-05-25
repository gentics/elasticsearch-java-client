package com.gentics.elasticsearch;

import org.junit.BeforeClass;
import org.junit.ClassRule;

import com.gentics.elasticsearch.client.okhttp.ElasticsearchOkClient;
import com.gentics.elasticsearch.docker.ElasticsearchContainer;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

public class AbstractDockerTest {

	static {
		// Use slf4j instead of JUL
		System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
	}

	@ClassRule
	public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

	public static ElasticsearchOkClient<JsonObject> client;

	@BeforeClass
	public static void setupClient() {
		client = new ElasticsearchOkClient<>("http", "localhost", elasticsearch.getMappedPort(9200));
		client.setConverterFunction(JsonObject::new);
	}

}
