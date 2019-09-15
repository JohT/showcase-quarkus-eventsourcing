package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

/**
 * Provides services to replay projections.
 * 
 * @author JohT
 */
public interface QueryProjectionManagementService {

    /**
     * This method start a replay of all events for the given processing group ("name of the projection").
     * 
     * @param processingGroupName {@link String}
     */
    void replayProcessingGroup(String processingGroupName);

    /**
     * Returns the status of the the processing group.
     * 
     * @param processingGroupName {@link String}
     * @return QueryProjectionStatus
     */
    QueryProjectionStatus getStatus(String processingGroupName);
}
