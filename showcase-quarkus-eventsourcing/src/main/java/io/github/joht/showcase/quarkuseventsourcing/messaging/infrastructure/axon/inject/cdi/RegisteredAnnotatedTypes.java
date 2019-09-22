package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RegisteredAnnotatedTypes {

    private static final Logger LOGGER = Logger.getLogger(RegisteredAnnotatedTypes.class.getName());

    private static final int MAX_META_ANNOTATION_RECURSION_DEPTH = 3;
    private final Collection<Class<?>> allTypes = new HashSet<>();

	public static final RegisteredAnnotatedTypes ofStream(Stream<Class<?>> allTypes) {
		return ofClasses(allTypes.collect(Collectors.toList()));
	}

	public static final RegisteredAnnotatedTypes ofClasses(Collection<Class<?>> allTypes) {
		return new RegisteredAnnotatedTypes(allTypes);
	}

    RegisteredAnnotatedTypes(Collection<Class<?>> allTypes) {
		this.allTypes.addAll(allTypes);
	}

    public Stream<Class<?>> subtypeOf(Class<?> annotationClass) {
        return logged(allTypes.stream().filter(annotationClass::isAssignableFrom).distinct(), annotationClass);
    }

    public Stream<Class<?>> annotatedWith(Class<? extends Annotation> annotationClass) {
        return logged(allTypes.stream().filter(type -> isAnnotationPresent(annotationClass, type)).distinct(), annotationClass);
	}

	@SafeVarargs
    public final Stream<Class<?>> annotatedWithAnyOf(Class<? extends Annotation>... annotationClasses) {
        return Stream.of(annotationClasses).flatMap(this::annotatedWith).distinct();
	}

    public void forEachAnnotatedType(Class<? extends Annotation> annotationClass, Consumer<? super Class<?>> type) {
        annotatedWith(annotationClass).forEach(type);
    }

    public Predicate<? super Class<?>> without(Class<? extends Annotation> annotationClass) {
        return type -> !annotatedWith(annotationClass).anyMatch(type::equals);
    }

	private static boolean isAnnotationPresent(Class<? extends Annotation> annotationClass, Class<?> type) {
		if (isAnnotationPresent(type, annotationClass, 0)) {
			return true;
		}
		for (Method method : type.getDeclaredMethods()) {
			if (isAnnotationPresent(method, annotationClass, 0)) {
				return true;
			}
		}
		for (Field field : type.getDeclaredFields()) {
			if (isAnnotationPresent(field, annotationClass, 0)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType,
			int recursion) {
		if (element.isAnnotationPresent(annotationType)) {
			return true;
		}
		if (recursion > MAX_META_ANNOTATION_RECURSION_DEPTH) {
			return false;
		}
		for (Annotation annotation : element.getAnnotations()) {
			if (annotation.annotationType().getName().startsWith("java.lang.annotation")) {
				continue;
			}
			if (isAnnotationPresent(annotation.annotationType(), annotationType, ++recursion)) {
				return true;
			}
		}
		return false;
	}

    private static Stream<Class<?>> logged(Stream<Class<?>> stream, Class<?> queriedType) {
        return stream.peek(type -> LOGGER.fine(() -> "Found " + type.getName() + " as " + queriedType));
    }

	@Override
	public String toString() {
		return "RegisteredAnnotatedTypes [allTypes=" + allTypes + "]";
	}
}