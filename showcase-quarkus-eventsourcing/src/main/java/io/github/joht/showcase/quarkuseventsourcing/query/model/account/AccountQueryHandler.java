package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.query.account.AccountNicknameQuery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelQueryHandler;

@ApplicationScoped
public class AccountQueryHandler {

	@Inject
	AccountRepository repository;

	@QueryModelQueryHandler
	public Nickname getAccountNickname(AccountNicknameQuery query) {
		return repository.read(new AccountEntityKey(query.getAccountId())).getNickname();
	}
}