package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

class ExceptionCause {

	private final Throwable exception;

	public static final ExceptionCause of(Throwable exception) {
		return new ExceptionCause(exception);
	}

	protected ExceptionCause(Throwable exception) {
		this.exception = exception;
	}

	public RuntimeException unwrapped() {
		Throwable cause = exception.getCause();
		if (cause instanceof RuntimeException) {
			return (RuntimeException) cause;
		}
		if (cause == null) {
			return new IllegalStateException(exception.getMessage());
		}
		return new IllegalStateException(cause);
	}

	@Override
	public String toString() {
		return "ExceptionCause [exception=" + exception + "]";
	}
}