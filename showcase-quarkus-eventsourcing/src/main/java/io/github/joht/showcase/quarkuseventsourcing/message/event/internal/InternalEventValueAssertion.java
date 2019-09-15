package io.github.joht.showcase.quarkuseventsourcing.message.event.internal;

import java.util.function.Supplier;

public class InternalEventValueAssertion {

	private InternalEventValueAssertion() {
		super();
	}
	
	public static <T> T notNull(T value, Supplier<String> messageSupplier) {
		if (value == null) {
			throw assertionFailed(messageSupplier.get());
		}
		return value;
	}
	
	private static <T extends RuntimeException> T assertionFailed(String message) {
		throw new IllegalArgumentException(message);
	}
}
