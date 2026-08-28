package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

/**
 * Provides services to replay projections.
 */
public interface QueryProjectionManagementService {

    /**
     * Starts a replay of all events for the given processing group.
     *
     * @param processingGroupName {@link String}
     */
    void replayProcessingGroup(String processingGroupName);

    /**
     * Returns the status of the processing group.
     *
     * @param processingGroupName {@link String}
     * @return QueryProjectionStatus
     */
    QueryProjectionStatus getStatus(String processingGroupName);
}
