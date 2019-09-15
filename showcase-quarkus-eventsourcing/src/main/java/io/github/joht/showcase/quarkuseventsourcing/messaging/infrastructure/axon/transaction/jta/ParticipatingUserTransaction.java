package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.transaction.jta;

import java.util.logging.Logger;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;

/**
 * This transaction uses or contributes to an leading/driving transaction and
 * does not perform commit or rollback on its own.
 * 
 * @author JohT
 */
final class ParticipatingUserTransaction implements Transaction {

	private static final Logger LOGGER = Logger.getLogger(ParticipatingUserTransaction.class.getName());

	private final UserTransaction userTransaction;

	public static final Transaction usingUserTransaction(UserTransaction userTransaction) {
		return new ParticipatingUserTransaction(userTransaction);
	}

	private ParticipatingUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	@Override
	public void commit() {
		LOGGER.fine("participation transaction - commit ommited");
	}

	@Override
	public void rollback() {
		try {
			userTransaction.setRollbackOnly();
			LOGGER.fine("ParticipatingUserTransaction successfully marked for rollback");
		} catch (IllegalStateException | UnsupportedOperationException | SystemException e) {
			LOGGER.info("ParticipatingUserTransaction could not be marked for rollback: " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ParticipatingUserTransaction [userTransaction=" + userTransaction + "]";
	}
}