package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import static org.mockito.Mockito.verify;

import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventPublishingAdapterTest {

	@Mock
	EventGateway eventGateway;
	
	@InjectMocks
	EventPublishingAdapter adapterUnderTest;
	
	@Test
	void eventPublishDelegatedToEventGateway() {
		TestEvent testEvent = new TestEvent();
		adapterUnderTest.publish(testEvent);
		verify(eventGateway).publish(testEvent);
	}

	private static class TestEvent {
		
	}
}
