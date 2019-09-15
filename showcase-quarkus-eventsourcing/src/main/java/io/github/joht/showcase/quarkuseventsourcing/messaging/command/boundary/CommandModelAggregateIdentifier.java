package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.modelling.command.AggregateIdentifier;

/**
 * Marks an Aggregate (Domain Driven Design), that can be used for CQRS and event-souring.
 * <p>
 * This annotation is based on axon framework's "@AggregateIdentifier".
 * 
 * @see AggregateIdentifier
 */
@Documented
@AggregateIdentifier
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandModelAggregateIdentifier {
	/**
	 * Get the name of the routing key property on commands that provides the
	 * identifier that should be used to target the aggregate root with the
	 * annotated field.
	 * <p>
	 * Optional. If left empty this defaults to field name.
	 */
	String routingKey() default "";
}
