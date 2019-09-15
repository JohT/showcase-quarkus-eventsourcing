package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbGlobalSequenceTrackingTokenAdapter;

import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonbGlobalSequenceTrackingTokenAdapterTest {

    private JsonbSerializationHelper serializer;

    /**
     * class under test
     */
    private JsonbGlobalSequenceTrackingTokenAdapter adapterToTest;

    @BeforeEach
    void setUp() {
        adapterToTest = new JsonbGlobalSequenceTrackingTokenAdapter();
        serializer = JsonbSerializationHelper.forAdapter(adapterToTest);
    }

    @Test
    void globalSequenceTrackingTokenZeroIndexSerializable() {
        TrackingToken token = new GlobalSequenceTrackingToken(0);
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }

    @Test
    void globalSequenceTrackingTokenMaxIndexSerializable() {
        TrackingToken token = new GlobalSequenceTrackingToken(Long.MAX_VALUE);
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }
}