package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbGapAwareTrackingTokenAdapter;

import java.util.Arrays;
import java.util.Collections;

import org.axonframework.eventhandling.GapAwareTrackingToken;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonbGapAwareTrackingTokenAdapterTest {

    private JsonbSerializationHelper serializer;

    /**
     * class under test
     */
    private JsonbGapAwareTrackingTokenAdapter adapterToTest;

    @BeforeEach
    void setUp() {
        adapterToTest = new JsonbGapAwareTrackingTokenAdapter();
        serializer = JsonbSerializationHelper.forAdapter(adapterToTest);
    }

    @Test
    void gapAwareTrackingTokenZeroIndexSerializable() {
        TrackingToken token = GapAwareTrackingToken.newInstance(0, Collections.emptyList());
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }

    @Test
    void gapAwareTrackingTokenMaxIndexSerializable() {
        TrackingToken token = GapAwareTrackingToken.newInstance(Long.MAX_VALUE, Collections.emptyList());
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }

    @Test
    void gapAwareTrackingTokenWithGapListSerializable() {
        TrackingToken token = GapAwareTrackingToken.newInstance(10, Arrays.asList(1L, 2L, 3L));
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }
}