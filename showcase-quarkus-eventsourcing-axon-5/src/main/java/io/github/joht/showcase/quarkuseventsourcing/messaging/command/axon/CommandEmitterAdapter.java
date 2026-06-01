package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import jakarta.transaction.Transactional;

import org.axonframework.messaging.commandhandling.CommandExecutionException;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandEmitterService;

public class CommandEmitterAdapter implements CommandEmitterService {

    private CommandGateway commandGateway;

    public CommandEmitterAdapter(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(REQUIRED)
    public <R> R sendAndWaitFor(Object command) throws IllegalStateException {
        try {
            return (R) commandGateway.sendAndWait(command);
        } catch (CommandExecutionException e) {
            throw ExceptionCause.of(e).unwrapped();
        }
    }

    @Override
    public String toString() {
        return "CommandEmitterAdapter [commandGateway=" + commandGateway + "]";
    }
}
