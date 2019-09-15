package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import java.beans.ConstructorProperties;

import javax.validation.constraints.NotNull;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

public class ChangeNicknameCommand {

	@NotNull
	@CommandTargetAggregateIdentifier
	private final String accountId;

	@NotNull
	private final Nickname nickname;

	@ConstructorProperties({ "accountId", "nickname" })
	public ChangeNicknameCommand(String accountId, Nickname nickname) {
		this.accountId = accountId;
		this.nickname = nickname;
	}

	public String getAccountId() {
		return accountId;
	}

	public Nickname getNickname() {
		return nickname;
	}

	@Override
	public String toString() {
		return "ChangeNicknameCommand [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}