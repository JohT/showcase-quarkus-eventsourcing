package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.transaction.Transactional;

import org.axonframework.eventhandling.gateway.EventGateway;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.EventPublishingService;

public class EventPublishingAdapter implements EventPublishingService {

	private final EventGateway eventGateway;

	public EventPublishingAdapter(EventGateway eventGateway) {
		this.eventGateway = eventGateway;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(REQUIRED)
	public void publish(Object... events) {
		eventGateway.publish(events);
	}

	@Override
	public String toString() {
		return "EventPublishingAdapter [eventGateway=" + eventGateway + "]";
	}
}
