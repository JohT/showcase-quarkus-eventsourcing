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

import org.axonframework.config.ProcessingGroup;

/**
 * Hint for the Configuration API that the annotated Event Handler object should be assigned to an Event Processor with the specified name.
 * <p>
 * This annotation is based on axon framework's "@ProcessingGroup".
 * 
 * @see ProcessingGroup
 */
// Note: For meta-annotations (here for @ProcessingGroup) with value() parameter,
// the parameter needs to be renamed to the simple class name of the original annotation.
// Thus, "value" cannot be used here. It needs to be defined as processingGroup().
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Stereotype
@Dependent
@Documented
@ProcessingGroup("")
public @interface QueryModelProjection {

    /**
     * The name of the Event Processor to assign the annotated Event Handler object to.
     *
     * @return the name of the Event Processor to assign objects of this type to
     */
    // Note: processingGroup can be optional, as soon as this issue is fixed:
    // https://github.com/AxonFramework/AxonFramework/issues/940
    String processingGroup();

    /**
     * Specifies the processor name that will be assigned to the {@link #processingGroup()}.<br>
     * The processor name represents a processing configuration.
     * <p>
     * Defaults to {@link QueryProcessor#TRACKING}
     * 
     * @return {@link QueryProcessor}
     */
    QueryProcessor processor() default QueryProcessor.TRACKING;

    /**
     * Gets the assignment between {@link QueryModelProjection#processingGroup()} and {@link QueryModelProjection#processor()} for the given
     * {@link Class} type or {@link Optional#empty()}, if there is no Annotation present or there is no assignment (default settings).
     * 
     * @author JohT
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