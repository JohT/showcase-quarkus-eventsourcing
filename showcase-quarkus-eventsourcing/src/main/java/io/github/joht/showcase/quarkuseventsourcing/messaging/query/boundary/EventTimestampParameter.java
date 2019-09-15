package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;

import org.axonframework.eventhandling.Timestamp;

/**
 * Injects the event creation time into the annotated {@link Instant}-typed parameter of the event handler method.
 * <p>
 * This annotation is based on axon framework's "@Timestamp".
 * 
 * @see Timestamp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Timestamp
public @interface EventTimestampParameter {

}
