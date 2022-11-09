package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbGapAwareTrackingTokenAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbGlobalSequenceTrackingTokenAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbReplayTokenAdapter;

import java.util.Collections;

import jakarta.json.bind.JsonbConfig;

import org.axonframework.eventhandling.GapAwareTrackingToken;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.ReplayToken;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonbReplayTokenAdapterTest {

    private JsonbSerializationHelper serializer;

    /**
     * class under test
     */
    private JsonbReplayTokenAdapter adapterToTest;

    @BeforeEach
    void setUp() {
        JsonbConfig config = new JsonbConfig();

        adapterToTest = new JsonbReplayTokenAdapter(config);
        config.withAdapters(new JsonbGlobalSequenceTrackingTokenAdapter());
        config.withAdapters(new JsonbGapAwareTrackingTokenAdapter());
        config.withAdapters(adapterToTest);
        serializer = JsonbSerializationHelper.of(config);
    }

    @Test
    void replayTokenWithGapAwareTrackingTokensSerializable() {
        TrackingToken current = GapAwareTrackingToken.newInstance(0, Collections.emptyList());
        TrackingToken reset = GapAwareTrackingToken.newInstance(321, Collections.emptyList());
        ReplayToken token = new ReplayToken(reset, current);
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }

    @Test
    void replayTokenWithGlobalSequenceTrackingTokensSerializable() {
        TrackingToken current = new GlobalSequenceTrackingToken(0);
        TrackingToken reset = new GlobalSequenceTrackingToken(123);
        ReplayToken token = new ReplayToken(reset, current);
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }

    @Test
    void replayTokenWithMixedTokensSerializable() {
        TrackingToken current = GapAwareTrackingToken.newInstance(0, Collections.emptyList());
        TrackingToken reset = new GlobalSequenceTrackingToken(123);
        ReplayToken token = new ReplayToken(reset, current);
        assertEquals(token, serializer.serializeAndDeserialize(token));
    }
}