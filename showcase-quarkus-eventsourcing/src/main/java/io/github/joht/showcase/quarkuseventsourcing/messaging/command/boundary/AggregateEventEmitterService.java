package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

/**
 * Provides methods to send events from aggregates.
 * <p>
 * This service is mean't to replace direct static calls to "apply" inside aggregate command handlers. <br>
 * This service is not mean't to be used to send events outside an aggregate.<br>
 * 
 * @author JohT
 */
public interface AggregateEventEmitterService {

	/**
	 * Immediately applies (publishes) the event with the given payload on all entities part of this aggregate.
	 *
	 * @param payload the payload of the event to apply
	 */
	void apply(Object payload);
}
