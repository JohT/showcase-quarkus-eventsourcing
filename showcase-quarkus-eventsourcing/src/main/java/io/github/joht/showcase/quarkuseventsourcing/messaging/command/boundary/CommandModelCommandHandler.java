package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.commandhandling.CommandHandler;

/**
 * Marks a method as being a CommandHandler.
 * <p>
 * Every commands has exactly one CommandHandler. It makes sense to place CommandHandler-Methods inside the aggregate
 * they belong to. Inside an aggregate, every information is present to validate the command, execute it (change state)
 * and send an event when it was successful. It is also possible, to have separate CommandHandlers for special cases.
 * <p>
 * This annotation is based on axon framework's "@CommandHandler".
 * 
 * @see CommandHandler
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE })
@CommandHandler
public @interface CommandModelCommandHandler {

	/**
	 * The name of the Command this handler listens to. Defaults to the fully
	 * qualified class name of the payload type (i.e. first parameter).
	 *
	 * @return The command name
	 */
	String commandName() default "";

	/**
	 * The property of the command to be used as a routing key towards this command
	 * handler instance. If multiple handlers instances are available, a sending
	 * component is responsible to route commands with the same routing key value to
	 * the correct instance.
	 *
	 * @return The property of the command to use as routing key
	 */
	String routingKey() default "";

	/**
	 * The type of payload expected by this handler. Defaults to the expected types
	 * expresses by (primarily the first) parameters of the annotated Method or
	 * Constructor.
	 *
	 * @return the payload type expected by this handler
	 */
	Class<?> payloadType() default Object.class;
}
