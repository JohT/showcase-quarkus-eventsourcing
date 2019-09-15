package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetTriggeredEvent;

/**
 * Used to mark a method inside a event handler ("projection") to be called,
 * when a replay takes place and the projection needs to be reseted.
 */
@Documented
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EventHandler(payloadType = ResetTriggeredEvent.class)
public @interface QueryModelResetHandler {

}
