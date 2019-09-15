package io.github.joht.showcase.quarkuseventsourcing.domain.model;

import org.axonframework.modelling.command.AnnotationCommandTargetResolver;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Disabled;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;
import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateVersion;

/**
 * Internal class that prepares and configured the {@link AggregateTestFixture}.
 * 
 * @author JohT
 *
 * @param <T> The type of Aggregate tested in this Fixture
 */
@Disabled
public class PreconfiguredAggregateTestFixture<T> {

	private final AggregateTestFixture<T> aggregateFixture;

	public PreconfiguredAggregateTestFixture(Class<T> aggregateType) {
		this.aggregateFixture = new AggregateTestFixture<>(aggregateType);
		setUp();
	}

	private void setUp() {
		getFixture().registerCommandTargetResolver(commandTargetResolver());
		getFixture().registerInjectableResource(new AggregateEventServiceStub());
	}

	private static AnnotationCommandTargetResolver commandTargetResolver() {
		return AnnotationCommandTargetResolver.builder()
				.targetAggregateIdentifierAnnotation(CommandTargetAggregateIdentifier.class)
				.targetAggregateVersionAnnotation(CommandTargetAggregateVersion.class)
				.build();
	}

	public AggregateTestFixture<T> getFixture() {
		return aggregateFixture;
	}
}