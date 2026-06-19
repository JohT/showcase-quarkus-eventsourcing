package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import java.util.Arrays;

import jakarta.transaction.Transactional;

import org.axonframework.messaging.eventhandling.gateway.EventGateway;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.EventPublishingService;

public class EventPublishingAdapter implements EventPublishingService {

    private final EventGateway eventGateway;

    public EventPublishingAdapter(EventGateway eventGateway) {
        this.eventGateway = eventGateway;
    }

    @Override
    @Transactional(REQUIRED)
    public void publish(Object... events) {
        eventGateway.publish(Arrays.asList(events));
    }

    @Override
    public String toString() {
        return "EventPublishingAdapter [eventGateway=" + eventGateway + "]";
    }
}
