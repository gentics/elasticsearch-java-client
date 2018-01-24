package com.gentics.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;

import io.vertx.core.json.JsonObject;

public class DocumentMethodsTest {

	@ClassRule
	public static GenericContainer<?> elasticsearch = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:6.1.2").withEnv(
			"discovery.type", "single-node").withExposedPorts(9200).waitingFor(Wait.forHttp("/"));

	@Test
	public void testDocumentCreate() throws IOException {
		Client<JsonObject> client = new Client<>("http", "localhost", elasticsearch.getMappedPort(9200));
		client.setConverterFunction(JsonObject::new);

		client.createIndex("dummy", new JsonObject());
		client.storeDocument("blub", "default", "one", new JsonObject().put("key1", "value1"));

		JsonObject doc = client.readDocument("blub", "default", "one");
		assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
	}

	@Test
	public void testDocumentCreateAsync() throws IOException {
		Client<JsonObject> client = new Client<>("http", "localhost", elasticsearch.getMappedPort(9200));
		client.setConverterFunction(JsonObject::new);

		JsonObject doc = client.createIndexAsync("dummy", new JsonObject()).toCompletable()

				.andThen(client.storeDocumentAsync("blub", "default", "one", new JsonObject().put("key1", "value1"))).toCompletable()

				.andThen(client.readDocumentAsync("blub", "default", "one")).blockingGet();
		
		assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
	}
}
