package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import java.beans.ConstructorProperties;
import java.util.Objects;
import java.util.function.Supplier;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

public class ChangeNicknameCommand {

	@CommandTargetAggregateIdentifier
	private final String accountId;

	private final Nickname nickname;

	@ConstructorProperties({ "accountId", "nickname" })
	public ChangeNicknameCommand(String accountId, Nickname nickname) {
		this.accountId =  requireNonNull(accountId, () -> "accountId missing");;
		this.nickname =  requireNonNull(nickname, () -> "nickname missing");;
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

	@Override
	public int hashCode() {
		return Objects.hash(accountId, nickname);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		ChangeNicknameCommand other = (ChangeNicknameCommand) obj;
		return Objects.equals(accountId, other.accountId) && Objects.equals(nickname, other.nickname);
	}
	
	private static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return obj;
    }
}