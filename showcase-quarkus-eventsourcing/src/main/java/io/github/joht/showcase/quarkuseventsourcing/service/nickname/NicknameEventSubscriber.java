package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;

final class NicknameEventSubscriber implements Consumer<List<NicknameDetails>>, AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(NicknameEventSubscriber.class.getName());

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
            LOGGER.warning(() -> "SSE EventSink is closed");
			return;
		}
        LOGGER.finest(() -> "SSE About to send " + data);
        eventSink.send(serverSentEvents.newEventBuilder()
                .id(Long.toString(data.getSequenceNumber()))
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(data)
                .reconnectDelay(5000)
				.build());
	}

	@Override
	public String toString() {
		return "NicknameEventSubscriber [eventSink=" + eventSink + "]";
	}
}