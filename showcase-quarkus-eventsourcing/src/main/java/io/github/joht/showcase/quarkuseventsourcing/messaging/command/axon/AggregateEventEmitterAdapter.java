package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import jakarta.enterprise.context.ApplicationScoped;

import org.axonframework.modelling.command.AggregateLifecycle;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;

@ApplicationScoped
public class AggregateEventEmitterAdapter implements AggregateEventEmitterService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply(Object payload) {
		AggregateLifecycle.apply(payload);
	}
}