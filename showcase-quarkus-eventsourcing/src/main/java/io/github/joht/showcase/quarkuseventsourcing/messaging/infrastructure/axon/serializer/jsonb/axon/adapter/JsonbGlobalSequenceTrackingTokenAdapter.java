package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import org.axonframework.eventhandling.GlobalSequenceTrackingToken;

/**
 * Note: This adapter is only needed for axon 4.1 or lower. Since axon 4.2 it is "included".
 * 
 * @author JohT
 */
class JsonbGlobalSequenceTrackingTokenAdapter implements JsonbAdapter<GlobalSequenceTrackingToken, JsonObject> {

    private static final JsonbAdapter<GlobalSequenceTrackingToken, JsonObject> STANDARD = new JsonbGlobalSequenceTrackingTokenAdapter();

    public static final JsonbAdapter<GlobalSequenceTrackingToken, JsonObject> standard() {
        return STANDARD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject adaptToJson(GlobalSequenceTrackingToken obj) {
        return Json.createObjectBuilder().add("globalIndex", obj.getGlobalIndex()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalSequenceTrackingToken adaptFromJson(JsonObject obj) {
        return new GlobalSequenceTrackingToken(obj.getJsonNumber("globalIndex").longValue());
    }

}
