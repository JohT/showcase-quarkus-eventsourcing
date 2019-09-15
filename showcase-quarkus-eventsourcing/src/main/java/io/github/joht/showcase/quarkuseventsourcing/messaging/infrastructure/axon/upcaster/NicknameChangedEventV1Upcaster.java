package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.upcaster;

import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonObject;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;

import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;

/**
 * Updates {@link NicknameChangedEvent} without revision to revision V1 by simply adding a missing field.
 * <p>
 * Note: Even tough this is a very simple upcaster, <br>
 * it is not easy to separate business concerns like new event fields <br>
 * from technical concerns, like upcasting serialized data.<br>
 * Since upcasting is applied directly on the serialized data, <br>
 * the upcaster also depends on the serializer.
 * 
 * @author JohT
 */
@Dependent
public class NicknameChangedEventV1Upcaster extends SingleEventUpcaster {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        SerializedType payloadType = intermediateRepresentation.getData().getType();
        return NicknameChangedEvent.EVENT_NAME.equals(payloadType.getName()) && (payloadType.getRevision() == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return intermediateRepresentation.upcastPayload(
                new SimpleSerializedType(NicknameChangedEvent.EVENT_NAME, "1"),
                JsonObject.class,
                event -> Json.createObjectBuilder(event)
                        .addAll(Json.createObjectBuilder(NicknameChangedEvent.MISSING_FIELDS_TO_V1))
                        .build());
    }
}