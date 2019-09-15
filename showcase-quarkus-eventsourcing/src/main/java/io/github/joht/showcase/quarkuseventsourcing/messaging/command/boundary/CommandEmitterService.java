package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;

/**
 * Provides methods to send command messages.
 *
 * @author JohT
 */
public interface CommandEmitterService {

	/**
	 * Sends the given {@code command} synchronously (blocks until complete).<br>
	 * is interrupted, this method returns {@code null}.
	 *
	 * @param command The command to dispatch
	 * @param         <R> The type of result expected from command execution
	 * @return the result of command execution, or {@code null} if the thread was interrupted while waiting for the command
	 *         to execute
	 * @throws IllegalStateException when an checked exception occurred while processing the command
	 */
	<R> R sendAndWaitFor(Object command);
}