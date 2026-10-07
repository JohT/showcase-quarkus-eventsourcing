package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import java.util.logging.Logger;

import jakarta.transaction.Transactional;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionManagementService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus;

/**
 * Stub implementation of {@link QueryProjectionManagementService} for Axon 5.
 * <p>
 * Replay functionality via pooled streaming event processors is not wired in this MVP.
 * The replay endpoint will log the request and return an empty status.
 */
public class QueryReplayAdapter implements QueryProjectionManagementService {

    private static final Logger LOGGER = Logger.getLogger(QueryReplayAdapter.class.getName());

    @Transactional(REQUIRED)
    @Override
    public void replayProcessingGroup(String processingGroupName) {
        LOGGER.info("Replay of " + processingGroupName + " requested (stub: not implemented in Axon 5 MVP)");
    }

    @Override
    public QueryProjectionStatus getStatus(String processingGroupName) {
        return QueryProjectionStatus.of();
    }

    @Override
    public String toString() {
        return "QueryReplayAdapter [stub]";
    }
}
