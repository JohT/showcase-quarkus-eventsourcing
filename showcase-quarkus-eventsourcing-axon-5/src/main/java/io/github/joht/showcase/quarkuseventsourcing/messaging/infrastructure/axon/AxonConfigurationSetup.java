package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;

import org.axonframework.common.configuration.AxonConfiguration;
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.eventhandling.gateway.EventGateway;
import org.axonframework.messaging.queryhandling.QueryUpdateEmitter;
import org.axonframework.messaging.queryhandling.configuration.QueryHandlingModule;
import org.axonframework.messaging.queryhandling.gateway.QueryGateway;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon.AggregateEventEmitterServiceParameterResolverFactory;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon.CommandEmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandEmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.AxonComponentDiscovery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.EventPublishingAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QueryReplayAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QuerySubmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QueryUpdateEmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.EventPublishingService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionManagementService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryUpdateEmitterService;

@Typed()
@ApplicationScoped
public class AxonConfigurationSetup {

    @Inject
    AxonComponentDiscovery componentDiscovery;

    private AxonConfiguration axonConfiguration;

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void startUp() {
        Class<Object> aggregateClass = (Class<Object>) componentDiscovery.findAggregateClass();
        List<Object> queryHandlerBeans = componentDiscovery.findQueryHandlerBeans();
        List<String> processingGroups = componentDiscovery.findProjectionProcessingGroups();

        EventSourcingConfigurer configurer = EventSourcingConfigurer.create()
                .registerEntity(EventSourcedEntityModule.autodetected(String.class, aggregateClass))
                .registerEventStorageEngine(config -> new InMemoryEventStorageEngine());

        for (Object queryHandlerBean : queryHandlerBeans) {
            final Object finalBean = queryHandlerBean;
            configurer.registerQueryHandlingModule(
                    QueryHandlingModule.named(finalBean.getClass().getSimpleName())
                            .queryHandlers()
                            .autodetectedQueryHandlingComponent(config -> finalBean));
        }

        configurer.messaging(messaging -> {
            messaging.registerParameterResolverFactory(
                    config -> new AggregateEventEmitterServiceParameterResolverFactory());
            messaging.eventProcessing(eventProcessing -> {
                eventProcessing.subscribing(subscribing -> {
                    var current = subscribing;
                    for (String processingGroup : processingGroups) {
                        final List<Object> projections = componentDiscovery.findProjectionBeans(processingGroup);
                        if (!projections.isEmpty()) {
                            current = current.defaultProcessor(processingGroup,
                                    cfg -> addProjectionsToProcessor(cfg, projections));
                        }
                    }
                    return current;
                });
            });
        });

        axonConfiguration = configurer.build();
        axonConfiguration.start();
    }

    @PreDestroy
    protected void shutdown() {
        axonConfiguration.shutdown();
    }

    @Produces
    @ApplicationScoped
    public CommandEmitterService getCommandEmitterService() {
        return new CommandEmitterAdapter(axonConfiguration.getComponent(CommandGateway.class));
    }

    @Produces
    @ApplicationScoped
    public QuerySubmitterService getQuerySubmitterService() {
        return new QuerySubmitterAdapter(axonConfiguration.getComponent(QueryGateway.class));
    }

    @Produces
    @ApplicationScoped
    public QueryUpdateEmitterService getQueryUpdateEmitterService() {
        return new QueryUpdateEmitterAdapter(axonConfiguration.getComponent(QueryUpdateEmitter.class));
    }

    @Produces
    @ApplicationScoped
    public QueryProjectionManagementService getQueryReplayService() {
        return new QueryReplayAdapter();
    }

    @Produces
    @ApplicationScoped
    public EventPublishingService getEventPublishingService() {
        return new EventPublishingAdapter(axonConfiguration.getComponent(EventGateway.class));
    }

    private static org.axonframework.messaging.eventhandling.configuration.EventHandlingComponentsConfigurer.CompletePhase addProjectionsToProcessor(
            org.axonframework.messaging.eventhandling.configuration.EventHandlingComponentsConfigurer.RequiredComponentPhase cfg,
            List<Object> projections) {
        var phase = cfg.autodetected(config -> projections.get(0));
        for (int index = 1; index < projections.size(); index++) {
            final Object projection = projections.get(index);
            phase = phase.autodetected(config -> projection);
        }
        return phase;
    }
}
