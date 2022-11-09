package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;

import org.axonframework.common.ObjectUtils;
import org.axonframework.serialization.AnnotationRevisionResolver;
import org.axonframework.serialization.ChainingConverter;
import org.axonframework.serialization.ContentTypeConverter;
import org.axonframework.serialization.Converter;
import org.axonframework.serialization.RevisionResolver;
import org.axonframework.serialization.SerializationException;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedObject;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.UnknownSerializedType;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.StringSerializer;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.JsonbStringSerializer;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter.JsonbAxonAdapterRegister;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.converter.JsonbAxonConverterRegister;

/**
 * {@link Serializer} for Axon using JSON-Binding (see http://json-b.net)
 * 
 * @author JohT
 */
public class JsonbSerializer implements Serializer {

    private static final Logger LOGGER = Logger.getLogger(JsonbSerializer.class.getName());

    private RevisionResolver revisionResolver = new AnnotationRevisionResolver();
    private Converter converter = new ChainingConverter();
    private StringSerializer stringSerializer = defaultJsonbSerializer().build();

    public static final Builder defaultSerializer() {
        return builder().stringSerializer(defaultJsonbSerializer().build());
    }

    public static final Builder fieldAccess() {
        StringSerializer stringSerializer = defaultJsonbSerializer().jsonbFieldAccess().build();
        return builder().stringSerializer(stringSerializer);
    }

    public static final Builder builder() {
        return new Builder();
    }

    private JsonbSerializer() {
        super();
    }

    private static JsonbStringSerializer.Builder defaultJsonbSerializer() {
        return JsonbStringSerializer.builder().jsonbConfig(JsonbAxonAdapterRegister::registeredAdapters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation) {
        try {
            if (String.class.equals(expectedRepresentation)) {
                return new SimpleSerializedObject<>((T) stringSerializer.serialize(object), expectedRepresentation,
                        typeForClass(ObjectUtils.nullSafeTypeOf(object)));
            }
            byte[] serializedBytes = stringSerializer.serialize(object).getBytes();
            T serializedContent = converter.convert(serializedBytes, expectedRepresentation);
            return new SimpleSerializedObject<>(serializedContent, expectedRepresentation,
                    typeForClass(ObjectUtils.nullSafeTypeOf(object)));
        } catch (JsonbException e) {
            throw new SerializationException("Unable to serialize object", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
        return String.class.equals(expectedRepresentation)
                || converter.canConvert(byte[].class, expectedRepresentation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T deserialize(SerializedObject<S> serializedObject) {
        try {
            if (SerializedType.emptyType().equals(serializedObject.getType())) {
                return null;
            }
            Class<?> type = classForType(serializedObject.getType());
            if (UnknownSerializedType.class.isAssignableFrom(type)) {
                return (T) new UnknownSerializedType(this, serializedObject);
            }
            SerializedObject<byte[]> byteSerialized = converter.convert(serializedObject, byte[].class);
            return (T) stringSerializer.deserialize(new String(byteSerialized.getData()), type);
        } catch (JsonbException e) {
            throw new SerializationException("Error while deserializing " + serializedObject, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Class classForType(SerializedType type) {
        if (SimpleSerializedType.emptyType().equals(type)) {
            return Void.class;
        }
        String classNameToResolve = resolveClassName(type);
        try {
            return Class.forName(classNameToResolve);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, e, () -> "Class " + classNameToResolve + " for type " + type + " not found");
            return UnknownSerializedType.class;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public SerializedType typeForClass(Class type) {
        if (type == null || Void.TYPE.equals(type) || Void.class.equals(type)) {
            return SimpleSerializedType.emptyType();
        }
        return new SimpleSerializedType(type.getName(), revisionResolver.revisionOf(type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Converter getConverter() {
        return converter;
    }

    /**
     * Resolve the class name from the given {@code serializedType}. This method may be overridden to customize the names used to denote
     * certain classes, for example, by leaving out a certain base package for brevity.
     *
     * @param serializedType The serialized type to resolve the class name for
     * @return The fully qualified name of the class to load
     */
    protected String resolveClassName(SerializedType serializedType) {
        return serializedType.getName();
    }

    /**
     * Returns the revision resolver used by this serializer.
     *
     * @return the revision resolver
     */
    protected RevisionResolver getRevisionResolver() {
        return revisionResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JsonbSerializer [revisionResolver=" + revisionResolver + ", converter=" + converter
                + ", stringSerializer=" + stringSerializer + "]";
    }

    public static class Builder {
        private JsonbSerializer serializer;

        public Builder() {
            this.serializer = new JsonbSerializer();
        }

        public Builder template(Serializer template) {
            converter(template.getConverter());
            return (template instanceof JsonbSerializer) ? templateJsonbSerializer((JsonbSerializer) template) : this;
        }

        private Builder templateJsonbSerializer(JsonbSerializer template) {
            revisionResolver(template.getRevisionResolver());
            stringSerializer(template.stringSerializer);
            return this;
        }

        /**
         * Overrides the data {@link Converter}. Defaults to {@link ChainingConverter}. Resets previously called
         * {@link #addContentTypeConverter(Class)}.
         * 
         * @param converter {@link Converter}
         * @return {@link Builder}
         */
        public Builder converter(Converter converter) {
            this.serializer.converter = converter;
            return this;
        }

        /**
         * Registers an additional chained {@link ContentTypeConverter}. Requires the default {@link ChainingConverter}.
         * 
         * @param contentTypeConverter {@link ContentTypeConverter}
         * @return {@link Builder}
         * @throws IllegalArgumentException when the default {@link ChainingConverter} had been exchanged by using
         *             {@link #converter(Converter)}.
         */
        public Builder addContentTypeConverter(ContentTypeConverter<?, ?> contentTypeConverter) {
            mandatoryChainingConverter().registerConverter(contentTypeConverter);
            return this;
        }

        /**
         * Registers an additional chained {@link ContentTypeConverter}. Requires the default {@link ChainingConverter}.
         * 
         * @param contentTypeConverter {@link Class} of a {@link ContentTypeConverter}
         * @return {@link Builder}
         * @throws IllegalArgumentException when the default {@link ChainingConverter} had been exchanged by using
         *             {@link #converter(Converter)}.
         */
        public Builder addContentTypeConverter(Class<? extends ContentTypeConverter<?, ?>> contentTypeConverter) {
            mandatoryChainingConverter().registerConverter(contentTypeConverter);
            return this;
        }

        /**
         * Registers additional chained {@link ContentTypeConverter}s. Requires the default {@link ChainingConverter}.
         * 
         * @param contentTypeConverters {@link Class} of a {@link ContentTypeConverter}
         * @return {@link Builder}
         * @throws IllegalArgumentException when the default {@link ChainingConverter} had been exchanged by using
         *             {@link #converter(Converter)}.
         */
        @SafeVarargs
        public final Builder addContentTypeConverters(Class<? extends ContentTypeConverter<?, ?>>... contentTypeConverters) {
            Stream.of(contentTypeConverters).forEach(this::addContentTypeConverter);
            return this;
        }

        private ChainingConverter mandatoryChainingConverter() {
            if (this.serializer.getConverter() instanceof ChainingConverter) {
                return ((ChainingConverter) this.serializer.getConverter());
            }
            throw new IllegalArgumentException("Requires a " + ChainingConverter.class.getSimpleName());
        }

        /**
         * Sets the {@link RevisionResolver}. Defaults to {@link AnnotationRevisionResolver}.
         * 
         * @param revisionResolver {@link RevisionResolver}
         * @return {@link Builder}
         */
        public Builder revisionResolver(RevisionResolver revisionResolver) {
            this.serializer.revisionResolver = revisionResolver;
            return this;
        }

        /**
         * Sets the {@link StringSerializer}. Defaults to {@link StringSerializer} from {@link JsonbBuilder#create()}.
         * {@link JsonbBuilder#create(JsonbConfig)}. <br>
         * Resets any previously changed {@link JsonbConfig} settings by {@link #jsonbConfig(JsonbConfig)},
         * {@link #mergeJsonbConfig(JsonbConfig)}, ...
         * 
         * @param stringSerializer - {@link StringSerializer}
         * @return {@link Builder}
         */
        public Builder stringSerializer(StringSerializer stringSerializer) {
            this.serializer.stringSerializer = stringSerializer;
            return this;
        }

        /**
         * Provides the built {@link JsonbSerializer}. May only be called once.
         * 
         * @return {@link JsonbSerializer}
         */
        public JsonbSerializer build() {
            try {
                addRegisteredConverters();
                return serializer;
            } finally {
                serializer = null;
            }
        }

        protected void addRegisteredConverters() {
            if (serializer.converter instanceof ChainingConverter) {
                JsonbAxonConverterRegister.registeredConverters().forEach(this::addContentTypeConverter);
            }
        }

        @Override
        public String toString() {
            return "Builder [serializer=" + serializer + "]";
        }
    }
}