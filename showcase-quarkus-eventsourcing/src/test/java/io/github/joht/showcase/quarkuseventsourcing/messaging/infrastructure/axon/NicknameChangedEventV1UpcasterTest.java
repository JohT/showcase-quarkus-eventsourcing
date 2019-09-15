package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.JsonbSerializer;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.upcaster.NicknameChangedEventV1Upcaster;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.axonframework.eventhandling.GenericDomainEventEntry;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NicknameChangedEventV1UpcasterTest {

    private static final String eventV0 = "{\"accountId\":\"1234-56789-0123456789\",\"nickname\":{\"value\":\"MyNickname\"}}";
    private static final String eventV1 = "{\"accountId\":\"1234-56789-0123456789\",\"nickname\":{\"value\":\"MyNickname\"},\"oldNickname\":{\"value\":\"\"}}";
    private static final String payloadType = "io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent";

    private NicknameChangedEventV1Upcaster testSubject;
    private Serializer serializer;

    @BeforeEach
    public void setUp() {
        testSubject = new NicknameChangedEventV1Upcaster();
        serializer = JsonbSerializer.defaultSerializer().build();
    }

    @Test
    public void shouldAddUnknownShopIdToOldRevisions() {
        InitialEventRepresentation initialEventRepresentation = new InitialEventRepresentation(new GenericDomainEventEntry<>("type",
                "aggregateIdentifier", 0, "eventId", Instant.now(), payloadType, null, eventV0.getBytes(), MetaData.emptyInstance()),
                serializer);
        List<IntermediateEventRepresentation> result = testSubject
                .upcast(Stream.<IntermediateEventRepresentation> of(initialEventRepresentation))
                .collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(eventV1, result.get(0).getData(String.class).getData());
        assertEquals("1", result.get(0).getData(String.class).getType().getRevision());
        assertEquals(payloadType, result.get(0).getData(String.class).getType().getName());
    }
}