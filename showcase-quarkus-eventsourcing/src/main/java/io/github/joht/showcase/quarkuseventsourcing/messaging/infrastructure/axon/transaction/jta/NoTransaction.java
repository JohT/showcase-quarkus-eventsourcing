package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.transaction.jta;

import org.axonframework.common.transaction.Transaction;

enum NoTransaction implements Transaction {

	INSTANCE {
		@Override
		public void commit() {
			// No action
		}

		@Override
		public void rollback() {
			// no action
		}
	},
	;
}
