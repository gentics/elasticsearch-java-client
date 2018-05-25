package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gentics.elasticsearch.AbstractDockerTest;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ClusterMethodsTest extends AbstractDockerTest {

	@Test
	public void testNodeInfo() throws HttpErrorException {
		JsonObject info = client.nodesInfo().sync();
		assertTrue(hasIngestPlugin(info));
	}

	private boolean hasIngestPlugin(JsonObject info) {
		JsonObject nodes = info.getJsonObject("nodes");
		for (String nodeId : nodes.fieldNames()) {
			JsonObject node = nodes.getJsonObject(nodeId);
			JsonArray plugins = node.getJsonArray("plugins");
			for (int i = 0; i < plugins.size(); i++) {
				JsonObject plugin = plugins.getJsonObject(i);
				String name = plugin.getString("name");
				if ("ingest-attachment".equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
}
