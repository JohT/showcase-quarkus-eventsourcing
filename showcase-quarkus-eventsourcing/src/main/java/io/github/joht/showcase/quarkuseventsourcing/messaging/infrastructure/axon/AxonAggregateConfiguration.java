package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;

import org.axonframework.config.AggregateConfigurer;
import org.axonframework.config.Configuration;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.modelling.command.AnnotationCommandTargetResolver;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;
import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateVersion;

/**
 * Configures all aggregates using {@link AggregateConfigurer}.
 * 
 * @author JohT
 */
class AxonAggregateConfiguration {

	private final AggregateConfigurer<?> aggregateConfigurer;

	public static final AggregateConfigurer<?> update(AggregateConfigurer<?> aggregateConfigurer) {
		return new AxonAggregateConfiguration(aggregateConfigurer).aggregateConfiguration();
	}

	private AxonAggregateConfiguration(AggregateConfigurer<?> aggregateConfigurer) {
		this.aggregateConfigurer = aggregateConfigurer;
	}

	private AggregateConfigurer<?> aggregateConfiguration() {
		aggregateConfigurer.configureSnapshotTrigger(config -> snapshotConfiguration(config));
		// Note: Using self defined annotations for "AggregateIdentifier" and
		// "AggregateVersion" located nearby the command
        // value objects and connecting them inside the axon configuration removes the
		// dependency between the API (containing
		// the commands) and "axon-modelling". The goal isn't, to share the API module
		// (better avoid that),
		// but to make the API as independent as possible.
		aggregateConfigurer.configureCommandTargetResolver(config -> annotationCommandTargetResolver());
		return aggregateConfigurer;
	}

	private static AnnotationCommandTargetResolver annotationCommandTargetResolver() {
		return AnnotationCommandTargetResolver.builder()
				.targetAggregateIdentifierAnnotation(CommandTargetAggregateIdentifier.class)
				.targetAggregateVersionAnnotation(CommandTargetAggregateVersion.class).build();
	}

	private EventCountSnapshotTriggerDefinition snapshotConfiguration(Configuration config) {
		return new EventCountSnapshotTriggerDefinition(snapshotter(config, aggregateConfigurer), 5);
	}

	private static <T> Snapshotter snapshotter(Configuration configuration, AggregateConfigurer<T> aggregate) {
		return AggregateSnapshotter.builder().eventStore(configuration.eventStore())
				.aggregateFactories(new GenericAggregateFactory<T>(aggregate.aggregateType())).build();
	}

	@Override
	public String toString() {
		return "AxonAggregateConfiguration [aggregateConfigurer=" + aggregateConfigurer + "]";
	}
}