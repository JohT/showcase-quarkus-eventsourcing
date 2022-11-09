package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Stereotype;

import org.axonframework.modelling.command.AggregateRoot;

/**
 * Marks an Aggregate (Domain Driven Design), that can be used for CQRS and event-souring. Such types will be the entry
 * point for command messages that target the aggregate.
 * <p>
 * This annotation is based on axon framework's "@AggregateRoot".
 * 
 * @see AggregateRoot
 */
@Documented
@Dependent
@Stereotype
@AggregateRoot
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandModelAggregate {
	/**
	 * Selects the name of the AggregateRepository bean. If left empty a new
	 * repository is created. In that case the name of the repository will be based
	 * on the simple name of the aggregate's class.
	 */
	String repository() default "";

	/**
	 * Get the String representation of the aggregate's type. Optional. This
	 * defaults to the simple name of the annotated class.
	 */
	String type() default "";
}
