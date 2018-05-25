package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.gentics.elasticsearch.AbstractDockerTest;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.vertx.core.json.JsonObject;

public class DocumentMethodsTest extends AbstractDockerTest {

	@Test
	public void testDocumentCreate() throws HttpErrorException {
		client.createIndex("dummy", new JsonObject()).sync();
		client.storeDocument("blub", "default", "one", new JsonObject().put("key1", "value1")).sync();

		JsonObject doc = client.readDocument("blub", "default", "one").sync();
		assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
	}

	@Test
	public void testDocumentCreateAsync() throws IOException {
		JsonObject doc = client.createIndex("dummy", new JsonObject()).async().toCompletable()
			.andThen(client.storeDocument("blub", "default", "one", new JsonObject().put("key1", "value1")).async().toCompletable())
			.andThen(client.readDocument("blub", "default", "one").async()).blockingGet();

		assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
	}
}
