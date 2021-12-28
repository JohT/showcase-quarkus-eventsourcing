package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

/**
 * Provides methods to publish events.
 * Based on the "EventGateway" in "AxonFramework".
 */
public interface EventPublishingService {

	/**
     * Publishes events that will be dispatched to all subscribed listeners.
     *
     * @param events The collection of events to publish
     */
	void publish(Object... events);
}
