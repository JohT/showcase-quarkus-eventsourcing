package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import jakarta.transaction.Transactional;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandEmitterService;

public class CommandEmitterAdapter implements CommandEmitterService {

    private CommandGateway commandGateway;

    public CommandEmitterAdapter(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    @Transactional(REQUIRED)
    public <R> R sendAndWaitFor(Object command) throws IllegalStateException {
        try {
            return commandGateway.sendAndWait(command);
        } catch (CommandExecutionException e) {
            throw ExceptionCause.of(e).unwrapped();
        }
    }

    @Override
    public String toString() {
        return "CommandEmitterAdapter [commandGateway=" + commandGateway + "]";
    }
}