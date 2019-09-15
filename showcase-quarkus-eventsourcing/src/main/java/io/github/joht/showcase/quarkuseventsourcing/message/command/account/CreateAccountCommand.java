package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import java.beans.ConstructorProperties;

import javax.validation.constraints.NotNull;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;

public class CreateAccountCommand {

	@NotNull
	@CommandTargetAggregateIdentifier
	private final String accountId;

	@ConstructorProperties({ "accountId" })
	public CreateAccountCommand(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	@Override
	public String toString() {
		return "CreateAccountCommand [accountId=" + accountId + "]";
	}
}