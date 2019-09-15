package io.github.joht.showcase.quarkuseventsourcing.message.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the (custom/local) revision of the event for upcasting purposes.
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventRevision {
    /**
     * The revision identifier for this object.
     */
    String value();
}
