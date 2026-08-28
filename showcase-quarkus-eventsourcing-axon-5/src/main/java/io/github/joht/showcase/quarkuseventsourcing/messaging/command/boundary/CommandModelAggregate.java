package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.eventsourcing.annotation.EventSourcedEntity;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon.CommandTargetAnnotationEntityIdResolverDefinition;

@Documented
@EventSourcedEntity(entityIdResolverDefinition = CommandTargetAnnotationEntityIdResolverDefinition.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandModelAggregate {

    String type() default "";
}
