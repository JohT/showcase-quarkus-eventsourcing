package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.concurrent.CompletableFuture;

import org.axonframework.common.Priority;
import org.axonframework.messaging.core.annotation.ParameterResolver;
import org.axonframework.messaging.core.annotation.ParameterResolverFactory;
import org.axonframework.messaging.core.unitofwork.ProcessingContext;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;

@Priority(Integer.MAX_VALUE)
public class AggregateEventEmitterServiceParameterResolverFactory implements ParameterResolverFactory {

    @Override
    public ParameterResolver<?> createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        if (AggregateEventEmitterService.class.equals(parameters[parameterIndex].getType())) {
            return new AggregateEventEmitterServiceParameterResolver();
        }
        return null;
    }

    private static class AggregateEventEmitterServiceParameterResolver
            implements ParameterResolver<AggregateEventEmitterService> {

        @Override
        public CompletableFuture<AggregateEventEmitterService> resolveParameterValue(ProcessingContext context) {
            return CompletableFuture.completedFuture(
                    new AggregateEventEmitterAdapter(EventAppender.forContext(context))
            );
        }

        @Override
        public boolean matches(ProcessingContext context) {
            return true;
        }
    }
}
