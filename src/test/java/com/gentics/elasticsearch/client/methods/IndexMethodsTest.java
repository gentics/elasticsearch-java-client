package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gentics.elasticsearch.AbstractDockerTest;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.vertx.core.json.JsonObject;

public class IndexMethodsTest extends AbstractDockerTest {

	@Test
	public void testIndexCreate() throws HttpErrorException {
		JsonObject response = client.createIndex("blub", new JsonObject()).sync();
		assertTrue(response.getBoolean("acknowledged"));

		JsonObject response2 = client.deleteIndex("blub").sync();
		assertTrue(response2.getBoolean("acknowledged"));
	}
}
