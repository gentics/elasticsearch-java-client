package com.gentics.elasticsearch.client.methods;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.gentics.elasticsearch.AbstractDockerTest;
import com.gentics.elasticsearch.client.HttpErrorException;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PipelineMethodsTest extends AbstractDockerTest {

	private static final String PIPELINE_NAME = "at.tac.hment";

	@Test
	public void testIngestPlugin() throws HttpErrorException {
		client.createIndex("blub", new JsonObject()).sync();

		client.registerPipeline(PIPELINE_NAME, getPipelineConfig(Arrays.asList("doc.data1", "doc.data3"))).sync();
		client.registerPipeline(PIPELINE_NAME + "2", getPipelineConfig(Arrays.asList("doc.data2"))).sync();
		JsonObject pipelines = client.listPipelines().sync();
		assertTrue(pipelines.containsKey(PIPELINE_NAME));
		assertTrue(pipelines.containsKey(PIPELINE_NAME + "2"));
		//client.deregisterPlugin(PIPELINE_NAME + "2").sync();

		// JsonObject doc = new JsonObject();
		// doc.put("data1", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");
		//// doc.put("data2", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");
		// doc.put("data2", new JsonObject().put("name", "atom"));
		// doc.put("data3", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");
		// client.storeDocument("blub", "default", "myid", doc).addQueryParameter("pipeline", PIPELINE_NAME).sync();

		StringBuffer buf = new StringBuffer();
		buf.append("{ \"index\" : {\"_id\" : \"myid\", \"_type\" : \"default\", \"pipeline\": \"" + PIPELINE_NAME + "\", \"_index\" : \"blub\"} }\n");
		buf.append("{ \"doc\" : {\"data1\" : \"e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=\"} }\n");

		buf.append(
			"{ \"index\" : {\"_id\" : \"myid2\", \"_type\" : \"default\", \"pipeline\": \"" + PIPELINE_NAME + "2" + "\", \"_index\" : \"blub\"} }\n");
		buf.append("{ \"doc\" : {\"data2\" : \"e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=\"} }\n");

		client.processBulk(buf.toString()).sync();

		System.out.println(client.getDocument("blub", "default", "myid").sync().encodePrettily());
		System.out.println(client.getDocument("blub", "default", "myid2").sync().encodePrettily());
	}

	private JsonObject getPipelineConfig(List<String> fields) {
		JsonObject config = new JsonObject();
		config.put("description", "Extract attachment information");

		JsonArray processors = new JsonArray();
		for (String field : fields) {
			JsonObject processor = new JsonObject();
			JsonObject settings = new JsonObject();
			settings.put("field", field);
			settings.put("target_field", "field." + field);
			settings.put("ignore_missing", true);
			processor.put("attachment", settings);
			processors.add(processor);
		}

		config.put("processors", processors);
		return config;
	}
}
