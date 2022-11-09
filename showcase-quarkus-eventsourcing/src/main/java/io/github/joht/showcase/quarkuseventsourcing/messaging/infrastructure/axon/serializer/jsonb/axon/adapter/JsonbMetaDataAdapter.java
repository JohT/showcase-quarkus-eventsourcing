package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import java.util.Map;

import jakarta.json.bind.adapter.JsonbAdapter;

import org.axonframework.messaging.MetaData;

/**
 * This adapter provides serialization support for the {@link MetaData} structure.
 * <p>
 * Since it is a generic structure, this adapter needs to be specified explicitly.
 * 
 * @author JohT
 */
class JsonbMetaDataAdapter implements JsonbAdapter<MetaData, Map<String, Object>> {

    private static final JsonbAdapter<MetaData, Map<String, Object>> STANDARD = new JsonbMetaDataAdapter();

    public static final JsonbAdapter<MetaData, Map<String, Object>> standard() {
        return STANDARD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> adaptToJson(MetaData obj) throws Exception {
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaData adaptFromJson(Map<String, Object> obj) throws Exception {
        return MetaData.from(obj);
    }
}
