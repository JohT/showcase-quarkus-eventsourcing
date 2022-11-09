package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.config.AggregateConfigurer;
import org.axonframework.config.Configuration;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateRoot;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.serialization.upcasting.event.EventUpcaster;

/**
 * Discovers all components for axon using CDI's {@link BeanManager#getBeans(String)}.
 * 
 * @author JohT
 */
@ApplicationScoped
public class AxonComponentDiscovery {

    @Inject
    BeanManager beanManager;

    /**
     * Attaches all discovered components to the given {@link AxonComponentDiscoveryContext#getConfigurer()}.
     * 
     * @param context {@link AxonComponentDiscoveryContext}
     */
    public void addDiscoveredComponentsTo(final AxonComponentDiscoveryContext context) {
        RegisteredAnnotatedTypes beanTypes = getBeanTypes();
        context.forEachDiscoveredAnnotation(beanTypes::forEachAnnotatedType);
        registerAggregates(context, beanTypes);
        registerEventHandlers(context, beanTypes);
        registerEventUpcasters(context, beanTypes);
        registerCommandHandlers(context, beanTypes);
        registerQueryHandlers(context, beanTypes);
        registerSagas(context, beanTypes);
        registerResourceInjector(context);
    }

    private void registerAggregates(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.annotatedWith(AggregateRoot.class)
                .map(AggregateConfigurer::defaultConfiguration).forEach(configurer -> {
                    context.onAggregateConfiguration().accept(configurer);
                    context.getConfigurer().configureAggregate(configurer);
                });
    }

    private void registerEventHandlers(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.annotatedWithAnyOf(ProcessingGroup.class, EventHandler.class)
                .map(this::lookedUp)
                .forEach(context.getConfigurer().eventProcessing()::registerEventHandler);
    }

    private void registerEventUpcasters(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.subtypeOf(EventUpcaster.class)
                .map(this::lookedUpEventUpcaster)
                .forEach(context.getConfigurer()::registerEventUpcaster);
    }

    private void registerCommandHandlers(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.annotatedWith(CommandHandler.class)
                .filter(beanTypes.without(AggregateRoot.class))
                .map(this::lookedUp)
                .forEach(context.getConfigurer()::registerCommandHandler);
    }

    private void registerQueryHandlers(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.annotatedWith(QueryHandler.class)
                .map(this::lookedUp)
                .forEach(context.getConfigurer()::registerQueryHandler);
    }

    private void registerSagas(AxonComponentDiscoveryContext context, RegisteredAnnotatedTypes beanTypes) {
        beanTypes.annotatedWith(SagaEventHandler.class)
                .forEach(context.getConfigurer().eventProcessing()::registerSaga);
    }

    private void registerResourceInjector(AxonComponentDiscoveryContext context) {
        context.getConfigurer().configureResourceInjector(CdiResourceInjector.useBeanManager(beanManager));
    }

    private Function<Configuration, Object> lookedUp(Class<?> typeToLookUp) {
        return config -> lookup(typeToLookUp);
    }

    private Function<Configuration, EventUpcaster> lookedUpEventUpcaster(Class<?> typeToLookUp) {
        return config -> (EventUpcaster) lookup(typeToLookUp);
    }

    private <U> Object lookup(Class<?> type, Annotation... qualifiers) {
        Bean<?> bean = beanManager.getBeans(type, qualifiers).iterator().next();
        CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        return beanManager.getReference(bean, type, ctx);
    }

    private RegisteredAnnotatedTypes getBeanTypes() {
        Set<Bean<?>> beans = beanManager.getBeans(Object.class, AnnotationLiteralAny.ANY);
        return RegisteredAnnotatedTypes.ofStream(beans.stream().filter(this::isBeanWithAtLeastOneType).map(Bean::getBeanClass));
    }

    private boolean isBeanWithAtLeastOneType(Bean<?> bean) {
        Typed annotation = bean.getBeanClass().getAnnotation(Typed.class);
        return (annotation != null) ? annotation.value().length > 0 : true;
    }

    private static class AnnotationLiteralAny extends AnnotationLiteral<Any> {
        private static final long serialVersionUID = 1L;
        public static final AnnotationLiteral<Any> ANY = new AnnotationLiteralAny();
    }
}