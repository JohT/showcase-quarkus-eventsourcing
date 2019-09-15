package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypes;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.FieldAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.FieldAnnotation;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MetaFieldAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MetaMetaMetaFieldAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MetaMethodAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MetaTypeAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MethodAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MethodAnnotation;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MultipleAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.MultipleMetaAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.NotAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.TypeAnnotated;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.RegisteredAnnotatedTypesTestFixture.TypeAnnotation;

class RegisteredAnnotatedTypesTest {

    /**
     * class under test.
     */
    private RegisteredAnnotatedTypes registered = RegisteredAnnotatedTypes.ofClasses(RegisteredAnnotatedTypesTestFixture.getAllTestClasses());

    @Test
    void shouldFindTypeAnnotatedClass() {
        assertTrue(registered.annotatedWith(TypeAnnotation.class).anyMatch(TypeAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMetaTypeAnnotatedClass() {
        assertTrue(registered.annotatedWith(TypeAnnotation.class).anyMatch(MetaTypeAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMethodAnnotatedClass() {
        assertTrue(registered.annotatedWith(MethodAnnotation.class).anyMatch(MethodAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMetaMethodAnnotatedClass() {
        assertTrue(registered.annotatedWith(MethodAnnotation.class).anyMatch(MetaMethodAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindFieldAnnotatedClass() {
        assertTrue(registered.annotatedWith(FieldAnnotation.class).anyMatch(FieldAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMetaFieldAnnotatedClass() {
        assertTrue(registered.annotatedWith(FieldAnnotation.class).anyMatch(MetaFieldAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMultipleAnnotatedClassWithAnyOfItsAnnotations() {
        assertTrue(registered.annotatedWith(TypeAnnotation.class).anyMatch(MultipleAnnotated.class::equals), registered::toString);
        assertTrue(registered.annotatedWith(MethodAnnotation.class).anyMatch(MultipleAnnotated.class::equals), registered::toString);
        assertTrue(registered.annotatedWith(FieldAnnotation.class).anyMatch(MultipleAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindMultipleMetaAnnotatedClassWithAnyOfItsAnnotations() {
        assertTrue(registered.annotatedWith(TypeAnnotation.class).anyMatch(MultipleMetaAnnotated.class::equals), registered::toString);
        assertTrue(registered.annotatedWith(MethodAnnotation.class).anyMatch(MultipleMetaAnnotated.class::equals), registered::toString);
        assertTrue(registered.annotatedWith(FieldAnnotation.class).anyMatch(MultipleMetaAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldntFindUnwantedTypes() {
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(NotAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(MethodAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(FieldAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(MetaMethodAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(MetaFieldAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldntFindTypeThatIsNotAnnotated() {
        assertFalse(registered.annotatedWith(TypeAnnotation.class).anyMatch(NotAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(MethodAnnotation.class).anyMatch(NotAnnotated.class::equals), registered::toString);
        assertFalse(registered.annotatedWith(FieldAnnotation.class).anyMatch(NotAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldBarelyFind3rdRecursionMetaFieldAnnotatedClass() {
        assertTrue(registered.annotatedWith(FieldAnnotation.class).anyMatch(MetaMetaMetaFieldAnnotated.class::equals), registered::toString);
    }

    @Test
    void shouldFindNothingWithoutAnContainedAnnotations() {
        List<Class<?>> withoutFieldAnnotated = registered
                .annotatedWith(TypeAnnotation.class)
                .filter(registered.without(FieldAnnotation.class))
                .collect(Collectors.toList());
        assertTrue(withoutFieldAnnotated.contains(TypeAnnotated.class), registered::toString);
        assertFalse(withoutFieldAnnotated.contains(FieldAnnotated.class), registered::toString);
        assertFalse(withoutFieldAnnotated.contains(MultipleAnnotated.class), registered::toString);
    }

    @Test
    void shouldFindAnyOfTheGivenAnnotatedTypes() {
        long notAnnotatedTypes = 1;
        long allTypes = RegisteredAnnotatedTypesTestFixture.getAllTestClasses().size();
        long foundTypes = registered.annotatedWithAnyOf(TypeAnnotation.class, FieldAnnotation.class, MethodAnnotation.class).count();
        assertEquals(allTypes - notAnnotatedTypes, foundTypes, registered::toString);
    }
}