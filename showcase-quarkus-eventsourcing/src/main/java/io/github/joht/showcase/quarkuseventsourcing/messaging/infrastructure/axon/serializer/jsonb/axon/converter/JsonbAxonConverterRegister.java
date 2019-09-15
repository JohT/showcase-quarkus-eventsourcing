package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.converter;

import static java.util.Arrays.asList;

import java.util.List;

import org.axonframework.serialization.ContentTypeConverter;

import static java.util.Collections.unmodifiableList;

/**
 * Contains all {@link ContentTypeConverter}s of this package.
 * 
 * @author JohT
 */
public class JsonbAxonConverterRegister {

    private static final List<ContentTypeConverter<?, ?>> CONVERTERS = unmodifiableList(asList(
            new JsonObject2StringConverter(),
            new String2JsonObjectConverter()));

    private JsonbAxonConverterRegister() {
        super();
    }

    public static List<ContentTypeConverter<?, ?>> registeredConverters() {
        return CONVERTERS;
    }
}
