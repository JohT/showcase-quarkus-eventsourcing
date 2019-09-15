package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.eventhandling.EventHandler;

/**
 * Annotation to be placed on methods that can handle events.
 * <p>
 * This annotation is based on axon framework's "@EventHandler".
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@EventHandler
public @interface QueryModelEventHandler {

	/**
	 * The type of event this method handles. This handler will only be considered
	 * for invocation if the event message's payload is assignable to this type.
	 * <p>
	 * Optional. If unspecified, the first parameter of the method defines the type
	 * of supported event.
	 *
	 * @return The type of the event this method handles.
	 */
	Class<?> payloadType() default Object.class;

}
