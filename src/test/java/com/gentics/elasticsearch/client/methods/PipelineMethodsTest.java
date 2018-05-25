package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gentics.elasticsearch.AbstractDockerTest;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PipelineMethodsTest extends AbstractDockerTest {

	private static final String PIPELINE_NAME = "attachment";

	@Test
	public void testIngestPlugin() throws HttpErrorException {
		client.createIndex("blub", new JsonObject()).sync();
		client.registerPipeline(PIPELINE_NAME, getPipelineConfig()).sync();
		client.registerPipeline(PIPELINE_NAME + "2", getPipelineConfig()).sync();
		JsonObject pipelines = client.listPipelines().sync();
		assertTrue(pipelines.containsKey(PIPELINE_NAME));
		assertTrue(pipelines.containsKey(PIPELINE_NAME + "2"));
		client.deregisterPlugin(PIPELINE_NAME + "2").sync();

		JsonObject doc = new JsonObject();
		doc.put("data", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");
		client.storeDocument("blub", "default", "myid", doc).addQueryParameter("pipeline", PIPELINE_NAME).sync();

		JsonObject result = client.getDocument("blub", "default", "myid").sync();
		System.out.println(result.encodePrettily());

	}

	private JsonObject getPipelineConfig() {
		JsonObject config = new JsonObject();
		config.put("description", "Extract attachment information");

		JsonObject processor = new JsonObject();
		processor.put(PIPELINE_NAME, new JsonObject().put("field", "data"));
		config.put("processors", new JsonArray().add(processor));
		return config;
	}
}
