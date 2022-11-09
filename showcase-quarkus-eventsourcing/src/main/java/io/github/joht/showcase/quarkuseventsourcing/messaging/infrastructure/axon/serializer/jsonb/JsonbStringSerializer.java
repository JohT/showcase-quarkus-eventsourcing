package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb;

import java.util.function.Consumer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.StringSerializer;

public class JsonbStringSerializer implements StringSerializer {

    private Jsonb jsonb;
    private JsonbConfig jsonbConfig = new JsonbConfig();

    public static final Builder builder() {
        return new Builder();
    }

    private JsonbStringSerializer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(Object object) {
        return jsonb.toJson(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return jsonb.fromJson(json, type);
    }

    @Override
    public String toString() {
        return "JsonbStringSerializer [jsonb=" + jsonb + ", jsonbConfig=" + jsonbConfig + "]";
    }

    public static final class Builder {

        private JsonbStringSerializer serializer = new JsonbStringSerializer();

        public Builder template(JsonbStringSerializer template) {
            return jsonb(template.jsonb).jsonbConfig(template.jsonbConfig);
        }

        /**
         * Overrides {@link Jsonb} and its {@link JsonbConfig}. Defaults to {@link JsonbBuilder#create(JsonbConfig)}. <br>
         * Resets any previously changed {@link JsonbConfig} settings by {@link #jsonbConfig(JsonbConfig)},
         * {@link #mergeJsonbConfig(JsonbConfig)}, ...
         * 
         * @param jsonb - {@link Jsonb}
         * @return {@link Builder}
         */
        public Builder jsonb(Jsonb jsonb) {
            this.serializer.jsonb = jsonb;
            return this;
        }

        /**
         * Overrides the {@link JsonbConfig}. Defaults to a plain new JsonbConfig.<br>
         * Resets previously called {@link #jsonb(Jsonb)}.<br>
         * Overrides any previously changed {@link JsonbConfig} settings by {@link #jsonbConfig(JsonbConfig)},
         * {@link #mergeJsonbConfig(JsonbConfig)}, ...
         * 
         * @param jsonbConfig - {@link JsonbConfig}
         * @return {@link Builder}
         */
        public Builder jsonbConfig(JsonbConfig jsonbConfig) {
            this.serializer.jsonb = null;
            this.serializer.jsonbConfig = jsonbConfig;
            return this;
        }

        /**
         * Merges the settings of the given {@link JsonbConfig} into the currently set ones.<br>
         * Resets previously called {@link #jsonb(Jsonb)}.<br>
         * 
         * @param jsonb - {@link JsonbConfig}
         * @return {@link Builder}
         */
        public Builder mergeJsonbConfig(JsonbConfig jsonbConfigToMerge) {
            this.serializer.jsonb = null;
            jsonbConfigToMerge.getAsMap().forEach(this.serializer.jsonbConfig::setProperty);
            return this;
        }

        /**
         * Takes a {@link Consumer} for further configuration of the {@link JsonbConfig}.<br>
         * Resets previously called {@link #jsonb(Jsonb)}.<br>
         * 
         * @param jsonbConfig - {@link JsonbConfig}
         * @return {@link Builder}
         */
        public Builder jsonbConfig(Consumer<JsonbConfig> jsonbConfigurer) {
            jsonbConfigurer.accept(this.serializer.jsonbConfig);
            return this;
        }

        /**
         * Sets field access as {@link PropertyVisibilityStrategy} using the {@link JsonbConfig}.
         * 
         * @param jsonbConfig - {@link JsonbConfig}
         * @return {@link Builder}
         */
        public Builder jsonbFieldAccess() {
            this.serializer.jsonb = null;
            this.serializer.jsonbConfig.withPropertyVisibilityStrategy(VisibilityStrategies.FIELD_VISIBILITY);
            return this;
        }

        /**
         * Provides the built {@link JsonbSerializer}. May only be called once.
         * 
         * @return {@link JsonbSerializer}
         */
        public StringSerializer build() {
            try {
                if (this.serializer.jsonb == null) {
                    jsonb(JsonbBuilder.create(this.serializer.jsonbConfig));
                }
                return this.serializer;
            } finally {
                this.serializer = null;
            }
        }
    }
}