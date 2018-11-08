package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.gentics.elasticsearch.AbstractDockerTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.IntStream;

public class InfoMethodsTest extends AbstractDockerTest {

	public void assertContainsIngestPlugin(JsonObject obj) {
		assertNotNull(obj);
		assertTrue(obj.containsKey("arrayData"));
		JsonArray plugins = obj.getJsonArray("arrayData");
		Optional<String> ingestPlugin = IntStream.range(0, plugins.size())
				.mapToObj(plugins::getJsonObject)
				.filter(o -> o.containsKey("component"))
				.map(o -> o.getString("component"))
				.filter("ingest-attachment"::equals)
				.findFirst();
		assertTrue(ingestPlugin.isPresent());
	}

	@Test
	public void testGetPluginsSync() throws Exception {
		assertContainsIngestPlugin(client.plugins().sync());
	}

	@Test
	public void testGetPluginsAsync() throws Exception {
		assertContainsIngestPlugin(client.plugins().async().blockingGet());
	}
}
