package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

public interface CommandEmitterService {

    <R> R sendAndWaitFor(Object command);
}
