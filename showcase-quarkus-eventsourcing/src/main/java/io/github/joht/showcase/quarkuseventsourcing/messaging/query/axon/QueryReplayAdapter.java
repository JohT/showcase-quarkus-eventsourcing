package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.transaction.Transactional;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventTrackerStatus;
import org.axonframework.eventhandling.TrackingEventProcessor;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionManagementService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus.Feature;

public class QueryReplayAdapter implements QueryProjectionManagementService {

    private static final Logger LOGGER = Logger.getLogger(QueryReplayAdapter.class.getName());

    private EventProcessingConfiguration eventProcessing;

    public QueryReplayAdapter(EventProcessingConfiguration eventProcessing) {
        this.eventProcessing = eventProcessing;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(REQUIRED)
    @Override
    public void replayProcessingGroup(String processingGroupName) {
        LOGGER.info("Replay of" + processingGroupName + " triggered using " + eventProcessing);
        getTrackingEventProcessor(processingGroupName).ifPresent(trackingEventProcessor -> {
            LOGGER.fine("Replay of" + processingGroupName + " in preparation");
            trackingEventProcessor.shutDown();
            trackingEventProcessor.resetTokens();
            trackingEventProcessor.start();
            LOGGER.fine("Replay of" + processingGroupName + " started");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryProjectionStatus getStatus(String processingGroupName) {
        Collection<EventTrackerStatus> eventTrackers = eventTrackerStatusCollection(processingGroupName);
        Collection<QueryProjectionStatus.Feature> features = new ArrayList<>();
        features.addAll(featuresOf(eventTrackers));
        if (getTrackingEventProcessor(processingGroupName).map(TrackingEventProcessor::isRunning).orElse(false)) {
            features.add(Feature.RUNNING);
        }
        return QueryProjectionStatus.ofAll(features);
    }

    protected Collection<Feature> featuresOf(Iterable<? extends EventTrackerStatus> eventTrackers) {
        Collection<Feature> features = new HashSet<>();
        features.add(Feature.CAUGHT_UP);
        for (EventTrackerStatus eventTrackerStatus : eventTrackers) {
            if (eventTrackerStatus.isReplaying()) {
                features.add(Feature.REPLAYING);
            }
            if (eventTrackerStatus.isErrorState()) {
                features.add(Feature.ERROR_STATE);
            }
            if (!eventTrackerStatus.isCaughtUp()) {
                features.remove(Feature.CAUGHT_UP);
            }
        }
        return features;
    }

    private Collection<EventTrackerStatus> eventTrackerStatusCollection(String processingGroupName) {
        return eventTrackerStatusForAll(processingGroupName).collect(Collectors.toList());
    }

    private Stream<EventTrackerStatus> eventTrackerStatusForAll(String processingGroupName) {
        return getTrackingEventProcessor(processingGroupName)
                .map(TrackingEventProcessor::processingStatus)
                .map(Map::values)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .peek(QueryReplayAdapter::logTrackerStatus);
    }

    private static void logTrackerStatus(EventTrackerStatus status) {
        LOGGER.finest("EventTrackerStatus:"
                + " segment:" + status.getSegment()
                + " caughtUp:" + status.isCaughtUp()
                + " replaying:" + status.isReplaying()
                + " error:" + status.getError());
    }

    private Optional<TrackingEventProcessor> getTrackingEventProcessor(String processingGroupName) {
        return eventProcessing.eventProcessorByProcessingGroup(processingGroupName, TrackingEventProcessor.class);
    }

    @Override
    public String toString() {
        return "QueryReplayAdapter [eventProcessing=" + eventProcessing + "]";
    }
}