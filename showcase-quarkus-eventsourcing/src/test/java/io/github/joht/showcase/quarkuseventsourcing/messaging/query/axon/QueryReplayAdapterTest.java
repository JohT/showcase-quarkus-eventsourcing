package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import java.util.Arrays;
import java.util.Collection;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventTrackerStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QueryReplayAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus.Feature;

@ExtendWith(MockitoExtension.class)
class QueryReplayAdapterTest {

    @Mock
    EventProcessingConfiguration configuration;

    @Mock
    EventTrackerStatus status;

    @InjectMocks
    QueryReplayAdapter adapterToTest;

    @Test
    void testCaughtUp() {
        when(status.isCaughtUp()).thenReturn(true);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertTrue(features.contains(Feature.CAUGHT_UP), features.toString());
    }

    @Test
    void testNotCaughtUp() {
        when(status.isCaughtUp()).thenReturn(false);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertFalse(features.contains(Feature.CAUGHT_UP), features.toString());
    }

    @Test
    void testReplaying() {
        when(status.isReplaying()).thenReturn(true);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertTrue(features.contains(Feature.REPLAYING), features.toString());
    }

    @Test
    void testNotReplaying() {
        when(status.isReplaying()).thenReturn(false);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertFalse(features.contains(Feature.REPLAYING), features.toString());
    }

    @Test
    void testErrorState() {
        when(status.isErrorState()).thenReturn(true);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertTrue(features.contains(Feature.ERROR_STATE), features.toString());
    }

    @Test
    void testNoErrorState() {
        when(status.isErrorState()).thenReturn(false);
        Collection<Feature> features = adapterToTest.featuresOf(Arrays.asList(status));
        assertFalse(features.contains(Feature.ERROR_STATE), features.toString());
    }
}