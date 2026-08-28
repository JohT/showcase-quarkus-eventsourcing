package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;

import java.lang.reflect.Field;

import org.axonframework.common.configuration.Configuration;
import org.axonframework.modelling.EntityIdResolver;
import org.axonframework.modelling.annotation.EntityIdResolverDefinition;
import org.axonframework.modelling.entity.annotation.AnnotatedEntityMetamodel;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;

public class CommandTargetAnnotationEntityIdResolverDefinition implements EntityIdResolverDefinition {

    @Override
    public <E, ID> EntityIdResolver<ID> createIdResolver(
            Class<E> entityType,
            Class<ID> idType,
            AnnotatedEntityMetamodel<E> entityMetamodel,
            Configuration configuration
    ) {
        return (message, context) -> {
            Object payload = message.payload();
            Class<?> payloadType = payload.getClass();
            for (Field field : payloadType.getDeclaredFields()) {
                if (field.isAnnotationPresent(CommandTargetAggregateIdentifier.class)) {
                    try {
                        field.setAccessible(true);
                        return idType.cast(field.get(payload));
                    } catch (IllegalAccessException exception) {
                        throw new IllegalStateException(
                                "Cannot access @CommandTargetAggregateIdentifier field "
                                        + field.getName() + " on " + payloadType.getName(),
                                exception
                        );
                    }
                }
            }
            throw new IllegalStateException(
                    "No @CommandTargetAggregateIdentifier found in " + payloadType.getName()
            );
        };
    }
}
