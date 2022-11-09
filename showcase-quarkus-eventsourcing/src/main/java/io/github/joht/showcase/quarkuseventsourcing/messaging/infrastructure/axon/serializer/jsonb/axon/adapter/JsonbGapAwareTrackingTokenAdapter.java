package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import java.util.Collection;
import java.util.stream.Collectors;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.bind.adapter.JsonbAdapter;

import org.axonframework.eventhandling.GapAwareTrackingToken;

/**
 * Note: This adapter is only needed for axon 4.1 or lower. Since axon 4.2 it is "included".
 * 
 * @author JohT
 */
class JsonbGapAwareTrackingTokenAdapter implements JsonbAdapter<GapAwareTrackingToken, JsonObject> {

    private static final JsonbAdapter<GapAwareTrackingToken, JsonObject> STANDARD = new JsonbGapAwareTrackingTokenAdapter();

    public static final JsonbAdapter<GapAwareTrackingToken, JsonObject> standard() {
        return STANDARD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject adaptToJson(GapAwareTrackingToken obj) {
        JsonArrayBuilder gaps = Json.createArrayBuilder();
        obj.getGaps().forEach(gaps::add);
        return Json.createObjectBuilder().add("gaps", gaps).add("index", obj.getIndex()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GapAwareTrackingToken adaptFromJson(JsonObject obj) {
        Collection<Long> gaps = obj.getJsonArray("gaps").getValuesAs(JsonNumber.class).stream()
                .map(JsonNumber::longValue).collect(Collectors.toList());
        return GapAwareTrackingToken.newInstance(obj.getJsonNumber("index").longValue(), gaps);
    }
}