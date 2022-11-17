package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.adapter;

import jakarta.json.bind.JsonbConfig;

public class JsonbAxonAdapterRegister {

    private JsonbAxonAdapterRegister() {
        super();
    }

    public static void registeredAdapters(JsonbConfig config) {
        config.withAdapters(//
                JsonbGapAwareTrackingTokenAdapter.standard(), //
                JsonbGlobalSequenceTrackingTokenAdapter.standard(), //
                JsonbMetaDataAdapter.standard(), //
                JsonbReplayTokenAdapter.using(config));
    }

}
