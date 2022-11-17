package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.transaction.jta;

import java.util.logging.Logger;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;

/**
 * Provides the {@link TransactionManager} for Axon.
 * 
 * @author JohT
 */
public class JtaTransactionManager implements TransactionManager {

	private static final Logger LOGGER = Logger.getLogger(JtaTransactionManager.class.getName());

	private final UserTransaction userTransaction;
	private final TransactionSynchronizationRegistry transactionRegistry;

	public static final TransactionManager using(UserTransaction user, TransactionSynchronizationRegistry registry) {
		return new JtaTransactionManager(user, registry);
	}

	protected JtaTransactionManager(UserTransaction userTransaction,
			TransactionSynchronizationRegistry transactionRegistry) {
		this.userTransaction = userTransaction;
		this.transactionRegistry = transactionRegistry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transaction startTransaction() {
		if (isTransactionynchronizationRegistryAvailable()) {
			return ParticipatingRegisteredTransaction.usingRegistry(transactionRegistry);
		}
		if (isUserTransactionAvailable()) {
			if (isInTransaction()) {
				return ParticipatingUserTransaction.usingUserTransaction(userTransaction);
			}
			return LeadingUserTransaction.usingUserTransaction(userTransaction);
		}
		return NoTransaction.INSTANCE;
	}

	private boolean isUserTransactionAvailable() {
		return (userTransaction != null);
	}

	private boolean isInTransaction() {
		try {
			return userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION;
		} catch (SystemException | IllegalStateException | UnsupportedOperationException e) {
			LOGGER.info("UserTransaction not availalbe: " + e.getMessage());
			return false;
		}
	}

	private boolean isTransactionynchronizationRegistryAvailable() {
		return transactionRegistry != null;
	}

	@Override
	public String toString() {
		return "JtaTransactionManager [userTransaction=" + userTransaction + ", transactionRegistry="
				+ transactionRegistry + "]";
	}
}