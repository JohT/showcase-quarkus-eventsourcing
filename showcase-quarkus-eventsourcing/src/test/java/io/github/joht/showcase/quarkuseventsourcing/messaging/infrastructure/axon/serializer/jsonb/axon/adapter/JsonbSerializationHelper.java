package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import java.util.function.Consumer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;

class JsonbSerializationHelper {

    private final Jsonb jsonb;

    public static final JsonbSerializationHelper standard() {
        return new JsonbSerializationHelper(JsonbBuilder.create(new JsonbConfig()));
    }

    public static final JsonbSerializationHelper of(JsonbConfig config) {
        return new JsonbSerializationHelper(JsonbBuilder.create(config));
    }

    public static final JsonbSerializationHelper forAdapter(JsonbAdapter<?, ?>... adapters) {
        return of(new JsonbConfig().withAdapters(adapters));
    }

    JsonbSerializationHelper(Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    public <T> T serializeAndDeserialize(T object) {
        return serializeAndDeserialize(object, json -> {
        });
    }

    public <T> T serializeAndDeserialize(T object, Consumer<String> jsonConsumer) {
        String json = serialize(object);
        jsonConsumer.accept(json);
        return deserialize(object, json);
    }

    private <T> String serialize(T object) {
        try {
            return jsonb.toJson(object);
        } catch (JsonbException e) {
            throw new IllegalStateException("object: " + object, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(T object, String json) {
        try {
            return (T) jsonb.fromJson(json, object.getClass());
        } catch (JsonbException e) {
            throw new IllegalStateException("json: " + json + ", object: " + object, e);
        }
    }

    @Override
    public String toString() {
        return "JsonbSerializationHelper [jsonb=" + jsonb + "]";
    }
}