package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.upcaster;

import org.axonframework.serialization.AnnotationRevisionResolver;
import org.axonframework.serialization.Revision;
import org.axonframework.serialization.RevisionResolver;

import io.github.joht.showcase.quarkuseventsourcing.message.event.EventRevision;

/**
 * Revision Resolver implementation that checks for the presence of an {@link Revision EventRevision} annotation.
 * <p>
 * The implementation is based on axons AnnotationRevisionResolver.
 * 
 * @author JohT
 * @see AnnotationRevisionResolver
 */
public class AnnotationEventRevisionResolver implements RevisionResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public String revisionOf(Class<?> payloadType) {
        EventRevision revision = payloadType.getAnnotation(EventRevision.class);
        if (revision != null) {
            return revision.value();
        }
        return null;
    }
}
