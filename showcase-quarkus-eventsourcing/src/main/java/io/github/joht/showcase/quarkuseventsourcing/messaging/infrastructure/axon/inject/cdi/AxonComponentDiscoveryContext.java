package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.axonframework.config.AggregateConfigurer;
import org.axonframework.config.Configurer;

/**
 * Provides the {@link Configurer} to attach all discovered components to the axon configuration.
 * <p>
 * Some of the components can be configured more detailed by their own configurations. <br>
 * {@link Consumer}s may optionally be specified for those. <br>
 * They will get called for every discovered component, that provides detailed configuration. <br>
 * The {@link Consumer}s are preset to do nothing, if not specified otherwise.
 * 
 * @author JohT
 */
public class AxonComponentDiscoveryContext {

	private static final Consumer<AggregateConfigurer<?>> NO_OPERATION_AGGREGATE_CONFIG = configurer -> {
	};
    private static final Consumer<Class<?>> NO_OPERATION_EVENT_HANDLER = type -> {
    };

	private Configurer configurer;
    private Consumer<AggregateConfigurer<?>> onAggregateConfiguration = NO_OPERATION_AGGREGATE_CONFIG;
    private final Map<Class<? extends Annotation>, Consumer<Class<?>>> onDiscoveredAnnotation = new HashMap<>();

	public static Builder builder() {
		return new Builder();
	}

	private AxonComponentDiscoveryContext() {
		super();
	}

	public Configurer getConfigurer() {
		return configurer;
	}

    public Consumer<AggregateConfigurer<?>> onAggregateConfiguration() {
		return onAggregateConfiguration;
	}

    public Consumer<Class<?>> onDiscoveredAnnotation(Class<? extends Annotation> annotationType) {
        return onDiscoveredAnnotation(annotationType);
    }

    public void forEachDiscoveredAnnotation(BiConsumer<? super Class<? extends Annotation>, ? super Consumer<Class<?>>> action) {
        onDiscoveredAnnotation.forEach(action);
    }

	@Override
    public String toString() {
        return "AxonComponentDiscoveryContext [configurer=" + configurer + ", onAggregateConfiguration=" + onAggregateConfiguration
                + ", onDiscoveredAnnotation=" + onDiscoveredAnnotation + "]";
    }

	public static final class Builder {
		private AxonComponentDiscoveryContext context = new AxonComponentDiscoveryContext();

        /**
         * Takes all settings of the given template {@link AxonComponentDiscoveryContext} <br>
         * enabling to change single settings an use all others of the template.
         * 
         * @param template {@link AxonComponentDiscoveryContext}
         * @return {@link Builder}
         */
        public Builder template(AxonComponentDiscoveryContext template) {
            configurer(template.getConfigurer());
            onAggregateConfiguration(template.onAggregateConfiguration());
            template.onDiscoveredAnnotation.forEach(this::onDiscoveredType);
            return this;
        }

		/**
		 * Sets the mandatory {@link Configurer}. May not be null
		 * 
		 * @param configurer
		 * @return
		 */
		public Builder configurer(Configurer configurer) {
			this.context.configurer = configurer;
			return this;
		}

		/**
		 * The given {@link Consumer} will be called for every aggregate that is
		 * discovered. This enables to specify further configurations for aggregates
		 * provided by the {@link AggregateConfigurer}.
		 * <p>
		 * Defaults to a "no operation" {@link Consumer} doing nothing when getting
		 * called.
		 * 
		 * @param configurationConsumer {@link Consumer} of {@link AggregateConfigurer}
		 * @return {@link Builder}
		 */
        public Builder onAggregateConfiguration(Consumer<AggregateConfigurer<?>> configurationConsumer) {
            this.context.onAggregateConfiguration = getOrDefault(configurationConsumer, NO_OPERATION_AGGREGATE_CONFIG);
			return this;
		}

		/**
         * The given {@link Consumer} will be called for every type that is discovered. <br>
         * This enables to specify custom actions that need to be done for all discovered types annotated with the given annotation.
         * <p>
         * Defaults to a "no operation" {@link Consumer} doing nothing when getting called.
         * 
         * @param annotationType {@link Class} that is a sub type of {@link Annotation}
         * @param typeConsumer {@link Consumer} of discovered {@link Class}
         * @return {@link Builder}
         */
        public Builder onDiscoveredType(Class<? extends Annotation> annotationType, Consumer<Class<?>> typeConsumer) {
            this.context.onDiscoveredAnnotation.put(annotationType, getOrDefault(typeConsumer, NO_OPERATION_EVENT_HANDLER));
            return this;
        }

        /**
         * Completes the build of {@link AxonComponentDiscoveryContext}. May only be used once per builder instance.
         * 
         * @return {@link AxonComponentDiscoveryContext}
         */
		public AxonComponentDiscoveryContext build() {
			notNull(this.context.getConfigurer(), "configurer may not be null");
			try {
				return context;
			} finally {
				context = null;
			}
		}

		@Override
		public String toString() {
			return "Builder [context=" + context + "]";
		}

        private static <T> T getOrDefault(T value, T defaultValue) {
            return (value != null) ? value : defaultValue;
        }

		private static <T> T notNull(T value, String message) {
			if (value == null) {
				throw new IllegalArgumentException(message);
			}
			return value;
		}
	}
}