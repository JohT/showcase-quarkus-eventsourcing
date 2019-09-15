package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.converter;

import javax.json.JsonObject;

import org.axonframework.serialization.ContentTypeConverter;

/**
 * Converts an JSON-P {@link JsonObject} into a JSON-{@link String}.
 * 
 * @author JohT
 */
class JsonObject2StringConverter implements ContentTypeConverter<JsonObject, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JsonObject> expectedSourceType() {
        return JsonObject.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> targetType() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convert(JsonObject original) {
        return original.toString();
    }
}