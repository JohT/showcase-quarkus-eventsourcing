package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deals with query results wrapped inside {@link CompletableFuture}s, <br>
 * that need to be provided synchronously (wait for the result).
 * 
 * @author JohT
 *
 * @param <T>
 */
class SynchonousQueryResult<T> {

	private static final long QUERYING_TIMEOUT_IN_SECONDS = 30;
	private static final Logger LOGGER = Logger.getLogger(SynchonousQueryResult.class.getName());

	private final CompletableFuture<T> future;

	public static final <T> SynchonousQueryResult<T> of(CompletableFuture<T> future) {
		return new SynchonousQueryResult<>(future);
	}

	protected SynchonousQueryResult(CompletableFuture<T> future) {
		this.future = future;
	}

	/**
	 * Waits for the result until the timeout for queries is reached or another Exception occured.
	 * 
	 * @param messageDetailsForErrors {@link String} in case of an unsuccessful query
	 * @return query result
	 */
	public T waitAndGet(String messageDetailsForErrors) {
		try {
			return future.get(QUERYING_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			throw logged(ExceptionCause.of(e).unwrapped(), messageDetailsForErrors);
		} catch (InterruptedException | TimeoutException e) {
			throw logged(new IllegalArgumentException(messageDetailsForErrors, e), messageDetailsForErrors);
		}
	}

	/**
	 * Return the original, wrapped {@link CompletableFuture}.
	 * 
	 * @return {@link CompletableFuture}
	 */
	public CompletableFuture<T> getFuture() {
		return future;
	}

	private static <E extends Throwable> E logged(E exception, String messagedetails) {
		LOGGER.log(Level.WARNING, messagedetails, exception);
		return exception;
	}

	@Override
	public String toString() {
		return "SynchonousQueryResult [future=" + future + "]";
	}
}