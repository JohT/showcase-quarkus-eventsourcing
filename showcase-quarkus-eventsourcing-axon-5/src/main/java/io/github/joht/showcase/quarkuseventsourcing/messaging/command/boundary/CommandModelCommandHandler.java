package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.messaging.commandhandling.annotation.CommandHandler;

@Documented
@CommandHandler
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandModelCommandHandler {

}
