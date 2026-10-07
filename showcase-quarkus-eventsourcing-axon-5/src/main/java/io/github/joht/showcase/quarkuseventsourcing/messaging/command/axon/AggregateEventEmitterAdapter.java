package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import org.axonframework.messaging.eventhandling.gateway.EventAppender;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;

public class AggregateEventEmitterAdapter implements AggregateEventEmitterService {

    private final EventAppender eventAppender;

    public AggregateEventEmitterAdapter(EventAppender eventAppender) {
        this.eventAppender = eventAppender;
    }

    @Override
    public void apply(Object payload) {
        eventAppender.append(payload);
    }
}
