package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelEventHandler;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProcessor;

@QueryModelProjection(processingGroup = "query.model.account", processor = QueryProcessor.SUBSCRIBING)
@Transactional(TxType.REQUIRED)
public class AccountProjection {

	@Inject
	AccountRepository account;

	@QueryModelEventHandler
	void onCreated(AccountCreatedEvent event) {
		account.create(keyOf(event.getAccountId()));
	}

	@QueryModelEventHandler
	void onNicknameChanged(NicknameChangedEvent event) {
		account.setNickname(keyOf(event.getAccountId()), event.getNickname());
	}

	private static AccountEntityKey keyOf(String accountId) {
		return new AccountEntityKey(accountId);
	}
}