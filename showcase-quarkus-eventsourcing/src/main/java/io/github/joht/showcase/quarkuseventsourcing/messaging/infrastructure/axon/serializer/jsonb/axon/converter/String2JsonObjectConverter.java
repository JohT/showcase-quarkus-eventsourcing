package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.converter;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.axonframework.serialization.ContentTypeConverter;

/**
 * Converts a JSON-{@link String} into an JSON-P {@link JsonObject}.
 * 
 * @author JohT
 */
class String2JsonObjectConverter implements ContentTypeConverter<String, JsonObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> expectedSourceType() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JsonObject> targetType() {
        return JsonObject.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject convert(String jsonString) {
        try (StringReader stringReader = new StringReader(jsonString);
                JsonReader jsonReader = Json.createReader(stringReader)) {
            return jsonReader.readObject();
        }
    }
}