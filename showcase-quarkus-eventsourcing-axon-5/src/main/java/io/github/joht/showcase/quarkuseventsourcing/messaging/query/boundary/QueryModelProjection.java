package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Stereotype;

/**
 * Marks a CDI bean as a query model projection (event handler).
 * <p>
 * In Axon 5, there is no @ProcessingGroup annotation. Processor assignment is done
 * programmatically in the AxonConfiguration using the processingGroup() and processor()
 * attributes of this annotation.
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Stereotype
@Dependent
@Documented
public @interface QueryModelProjection {

    /**
     * The name of the Event Processor to assign the annotated Event Handler object to.
     *
     * @return the name of the Event Processor to assign objects of this type to
     */
    String processingGroup();

    /**
     * Specifies the processor name that will be assigned to the {@link #processingGroup()}.
     * <p>
     * Defaults to {@link QueryProcessor#TRACKING}
     *
     * @return {@link QueryProcessor}
     */
    QueryProcessor processor() default QueryProcessor.TRACKING;

    /**
     * Gets the assignment between {@link QueryModelProjection#processingGroup()} and
     * {@link QueryModelProjection#processor()} for the given {@link Class} type or
     * {@link Optional#empty()}, if there is no annotation present or there is no assignment
     * (default settings).
     */
    public static class ProcessorAssignment implements Function<Class<?>, Optional<QueryModelProjection>> {

        public static final void forType(Class<?> type, Consumer<QueryModelProjection> consumer) {
            Stream.of(type).map(new ProcessorAssignment()).filter(Optional::isPresent).map(Optional::get).forEach(consumer);
        }

        @Override
        public Optional<QueryModelProjection> apply(Class<?> type) {
            if (!type.isAnnotationPresent(QueryModelProjection.class)) {
                return Optional.empty();
            }
            QueryModelProjection annotation = type.getAnnotation(QueryModelProjection.class);
            if (annotation.processor().isDefault()) {
                return Optional.empty();
            }
            if (annotation.processingGroup().isEmpty()) {
                String message = "Assigning the non default processor %s to %s requires an explicitly defined, non empty processing group.";
                throw new IllegalStateException(String.format(message, annotation.processor().toString(), type.getSimpleName()));
            }
            return Optional.of(annotation);
        }
    }
}
