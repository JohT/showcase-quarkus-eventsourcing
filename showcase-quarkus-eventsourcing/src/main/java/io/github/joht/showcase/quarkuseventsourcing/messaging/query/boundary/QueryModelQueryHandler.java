package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.queryhandling.QueryHandler;

/**
 * Mark a service method as being a QueryHandler.
 * <p>
 * The annotated method's first parameter is the query handled by that method.
 * <p>
 * This annotation is based on axon framework's "@QueryHandler".
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@QueryHandler
public @interface QueryModelQueryHandler {

	/**
	 * The name of the Query this handler listens to.
	 *
	 * @return The query name
	 */
	String queryName() default "";
}
