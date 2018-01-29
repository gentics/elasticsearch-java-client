# Minimalistic Elasticsearch Java Client

The aim of this project is to provide an alternative Java REST client for elasticsearch.

This client is model agnostic and thus does not provide any models. Use the [high level REST client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high.html) if you need those models.

The client is also JSON parser agnostic. You can configure your own JSON parser via the `Client#setConverterFunction` method.

## Async API via RxJava 2

```java
Client<JsonObject> client = new Client<>("http", "localhost", 9200);
client.setConverterFunction(JsonObject::new);

JsonObject doc = client.createIndex("dummy", new JsonObject()).async().toCompletable()
        .andThen(client.storeDocument("blub", "default", "one", new JsonObject().put("key1", "value1"))).async().toCompletable()
        .andThen(client.readDocument("blub", "default", "one").async()).blockingGet();

assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
```

## Blocking API

```java
Client<JsonObject> client = new Client<>("http", "localhost", 9200);
client.setConverterFunction(JsonObject::new);

client.createIndex("dummy", new JsonObject()).sync();
client.storeDocument("blub", "default", "one", new JsonObject().put("key1", "value1")).sync();

JsonObject doc = client.readDocument("blub", "default", "one").sync();
assertEquals("value1", doc.getJsonObject("_source").getString("key1"));
```

## TODOs

* Add Scroll API support 
* Support for multiple ES instances
* More tests