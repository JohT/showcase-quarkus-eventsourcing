package io.github.joht.showcase.quarkuseventsourcing.service.account;

import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;

final class NicknameEventSubscriber implements Consumer<List<NicknameDetails>>, AutoCloseable {

	private final Sse serverSentEvents;
	private final SseEventSink eventSink;

	public NicknameEventSubscriber(Sse sse, SseEventSink eventSink) {
		this.serverSentEvents = sse;
		this.eventSink = eventSink;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		eventSink.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(List<NicknameDetails> nextNicknameDetails) {
		nextNicknameDetails.forEach(this::sendAsEvent);
	}

	private <T> void sendAsEvent(NicknameDetails data) {
		if (eventSink.isClosed()) {
			return;
		}
        eventSink.send(serverSentEvents.newEventBuilder()
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(data)
                .id(Long.toString(data.getSequenceNumber()))
                .reconnectDelay(5000)
				.build());
	}

	@Override
	public String toString() {
		return "NicknameEventSubscriber [eventSink=" + eventSink + "]";
	}
}