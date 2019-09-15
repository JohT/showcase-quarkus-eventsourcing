package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.eventhandling.SequenceNumber;

/**
 * Injects the sequence number into a {@link Long}-typed Parameter of the event handler method.
 * <p>
 * This annotation is based on axon framework's "@SequenceNumber".
 * 
 * @see SequenceNumber
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@SequenceNumber
public @interface EventSequenceParameter {

}
