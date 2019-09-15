package io.github.joht.showcase.quarkuseventsourcing.message.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the identifier of the aggregate that a command targets.
 * <p>
 * This annotation is based on axon framework's "AggregateIdentifier".
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandTargetAggregateIdentifier {

}
