package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the Axon 5 stub {@link QueryReplayAdapter}.
 * <p>
 * Replay via pooled streaming event processors is not wired in the Axon 5 MVP;
 * the adapter logs the request and returns an empty status.
 */
class QueryReplayAdapterTest {

    QueryReplayAdapter adapterToTest = new QueryReplayAdapter();

    @Test
    void replayDoesNotThrow() {
        assertDoesNotThrow(() -> adapterToTest.replayProcessingGroup("nicknames"));
    }

    @Test
    void getStatusReturnsEmptyStatus() {
        QueryProjectionStatus status = adapterToTest.getStatus("nicknames");
        assertNotNull(status);
    }
}
