package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.transaction.jta;

import java.util.logging.Logger;

import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import org.axonframework.common.transaction.Transaction;

/**
 * This transaction uses or contributes to an leading/driving transaction <br>
 * and does not perform commit's or rollback's on its own.
 * 
 * @author JohT
 */
final class ParticipatingRegisteredTransaction implements Transaction {

	private static final Logger LOGGER = Logger.getLogger(ParticipatingRegisteredTransaction.class.getName());

	private final TransactionSynchronizationRegistry transactionRegistry;

	public static final Transaction usingRegistry(TransactionSynchronizationRegistry transactionRegistry) {
		return new ParticipatingRegisteredTransaction(transactionRegistry);
	}

	private ParticipatingRegisteredTransaction(TransactionSynchronizationRegistry transactionRegistry) {
		this.transactionRegistry = transactionRegistry;
	}

	@Override
	public void commit() {
		LOGGER.fine("participation transaction - commit ommited");
	}

	@Override
	public synchronized void rollback() {
		if (transactionRegistry.getTransactionStatus() == Status.STATUS_NO_TRANSACTION) {
			LOGGER.fine("TransactionSynchronizationRegistry no transaction to rollback");
			return;
		}
		try {
			transactionRegistry.setRollbackOnly();
			LOGGER.fine("TransactionSynchronizationRegistry successfully marked for rollback");
		} catch (IllegalStateException | UnsupportedOperationException e) {
			LOGGER.info("TransactionSynchronizationRegistry could not be marked for rollback: " + e);
		}
	}

	@Override
	public String toString() {
		return "ParticipatingRegisteredTransaction [transactionRegistry=" + transactionRegistry + "]";
	}
}