package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelAggregate;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelQueryHandler;

@ApplicationScoped
public class AxonComponentDiscovery {

    private final BeanManager beanManager;

    @Inject
    public AxonComponentDiscovery(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public Class<?> findAggregateClass() {
        return getAllBeanClasses()
                .filter(type -> type.isAnnotationPresent(CommandModelAggregate.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No class annotated with @CommandModelAggregate found"));
    }

    public List<String> findProjectionProcessingGroups() {
        return getAllBeanClasses()
                .filter(type -> type.isAnnotationPresent(QueryModelProjection.class))
                .map(type -> type.getAnnotation(QueryModelProjection.class).processingGroup())
                .distinct()
                .collect(toList());
    }

    public List<Object> findProjectionBeans(String processingGroup) {
        return getAllBeanClasses()
                .filter(type -> type.isAnnotationPresent(QueryModelProjection.class)
                        && type.getAnnotation(QueryModelProjection.class).processingGroup().equals(processingGroup))
                .map(this::lookup)
                .collect(toList());
    }

    public List<Object> findQueryHandlerBeans() {
        return getAllBeanClasses()
                .filter(this::hasQueryHandlerMethods)
                .map(this::lookup)
                .collect(toList());
    }

    private boolean hasQueryHandlerMethods(Class<?> type) {
        return Stream.of(type.getMethods())
                .anyMatch(method -> method.isAnnotationPresent(QueryModelQueryHandler.class));
    }

    private Stream<Class<?>> getAllBeanClasses() {
        Set<Bean<?>> beans = beanManager.getBeans(Object.class, AnyAnnotationLiteral.INSTANCE);
        return beans.stream()
                .filter(this::isNotTypedEmpty)
                .map(Bean::getBeanClass)
                .distinct();
    }

    private boolean isNotTypedEmpty(Bean<?> bean) {
        Typed typed = bean.getBeanClass().getAnnotation(Typed.class);
        return typed == null || typed.value().length > 0;
    }

    private Object lookup(Class<?> type) {
        Bean<?> bean = beanManager.getBeans(type).iterator().next();
        CreationalContext<?> context = beanManager.createCreationalContext(bean);
        return beanManager.getReference(bean, type, context);
    }

    private static final class AnyAnnotationLiteral extends AnnotationLiteral<Any> {
        static final AnyAnnotationLiteral INSTANCE = new AnyAnnotationLiteral();
        private static final long serialVersionUID = 1L;
    }
}
