package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

public interface AggregateEventEmitterService {

    void apply(Object payload);
}
