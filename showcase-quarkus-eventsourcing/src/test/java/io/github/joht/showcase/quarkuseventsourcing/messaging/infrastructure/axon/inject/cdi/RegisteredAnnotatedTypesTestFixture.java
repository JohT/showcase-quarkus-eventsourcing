package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;

@Disabled
class RegisteredAnnotatedTypesTestFixture {

    private RegisteredAnnotatedTypesTestFixture() {
        super();
    }

    public static Collection<Class<?>> getAllTestClasses() {
        return Arrays.asList(
                NotAnnotated.class,
                TypeAnnotated.class,
                MetaTypeAnnotated.class,
                MethodAnnotated.class,
                MetaMethodAnnotated.class,
                FieldAnnotated.class,
                MetaFieldAnnotated.class,
                MetaMetaMetaFieldAnnotated.class,
                MultipleAnnotated.class,
                MultipleMetaAnnotated.class
                );
    };

    class NotAnnotated {

    }

    @TypeAnnotation
    class TypeAnnotated {

    }

    @MetaTypeAnnotation
    class MetaTypeAnnotated {

    }

    class MethodAnnotated {
        @MethodAnnotation
        void aMethod() {

        };
    }

    class MetaMethodAnnotated {
        @MetaMethodAnnotation
        void aMethod() {

        };
    }

    class FieldAnnotated {
        @FieldAnnotation
        String aField;
    }

    class MetaFieldAnnotated {
        @MetaFieldAnnotation
        String aField;
    }

    class MetaMetaMetaFieldAnnotated {
        @MetaMetaMetaFieldAnnotation
        String aField;
    }

    @TypeAnnotation
    class MultipleAnnotated {
        @FieldAnnotation
        String aField;

        @MethodAnnotation
        void aMethod() {

        };
    }

    @MetaTypeAnnotation
    class MultipleMetaAnnotated {
        @MetaFieldAnnotation
        String aField;

        @MetaMethodAnnotation
        void aMethod() {

        };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @interface TypeAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
    @interface MethodAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @interface FieldAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @TypeAnnotation
    @interface MetaTypeAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @MethodAnnotation
    @interface MetaMethodAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @FieldAnnotation
    @interface MetaFieldAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @MetaFieldAnnotation
    @interface MetaMetaFieldAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @MetaMetaFieldAnnotation
    @interface MetaMetaMetaFieldAnnotation {
    }

}
