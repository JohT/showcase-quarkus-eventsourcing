package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;

import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CdiParameterResolverFactoryTest {

    // Note: Since there is a problem using ServiceLoader registry on quarkus in dev mode
    // in conjunction with ClasspathParameterResolverFactory, the registration is done inside AxonConfiguration.
    // That's why the ServiceLoader loading test is disabled.
    @Disabled
    @Test
    void assureRegisteredForServiceLoader() {
        List<ParameterResolverFactory> resolvers = new ArrayList<>();
        ServiceLoader.load(ParameterResolverFactory.class).forEach(resolvers::add);
        assertTrue(resolvers.stream().map(ParameterResolverFactory::getClass).anyMatch(CdiParameterResolverFactory.class::equals));
    }

    @Disabled
    private static final class CdiDummy<T> extends CDI<Object> {

        @Override
        public Instance<Object> select(Annotation... qualifiers) {
            return null;
        }

        @Override
        public <U> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
            return null;
        }

        @Override
        public <U> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
            return null;
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(Object instance) {
        }

        @Override
        public Iterator<Object> iterator() {
            return null;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public BeanManager getBeanManager() {
            return null;
        }
    }
}
