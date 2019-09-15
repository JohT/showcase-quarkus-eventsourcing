package io.github.joht.showcase.quarkuseventsourcing.domain.model;

import org.axonframework.modelling.command.AggregateLifecycle;
import org.junit.jupiter.api.Disabled;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;

@Disabled
class AggregateEventServiceStub implements AggregateEventEmitterService {

	@Override
	public void apply(Object payload) {
		AggregateLifecycle.apply(payload);
	}

}
