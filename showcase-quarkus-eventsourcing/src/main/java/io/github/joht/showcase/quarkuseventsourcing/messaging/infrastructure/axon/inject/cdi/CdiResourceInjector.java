package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.util.function.Function;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.InjectionTargetFactory;

import org.axonframework.modelling.saga.ResourceInjector;

public final class CdiResourceInjector implements ResourceInjector {

	public static final ResourceInjector standard() {
		return new CdiResourceInjector(CDI.current().getBeanManager());
	}

	public static final <P> Function<P, ResourceInjector> useBeanManager(BeanManager beanManager) {
		return p -> new CdiResourceInjector(beanManager);
	}

	private final BeanManager beanManager;

	private CdiResourceInjector(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void injectResources(Object resource) {
		if (resource != null) {
			injectResourcesUsingCdi(resource);
		}
	}

	private <T> void injectResourcesUsingCdi(T resource) {
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) resource.getClass();

		AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(type);
		InjectionTargetFactory<T> targetFactory = beanManager.getInjectionTargetFactory(annotatedType);
		InjectionTarget<T> target = targetFactory.createInjectionTarget(null);
		CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);
		target.inject(resource, creationalContext);
		target.postConstruct(resource);
	}
}