package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import java.beans.ConstructorProperties;
import java.util.Objects;
import java.util.function.Supplier;

import io.github.joht.showcase.quarkuseventsourcing.message.command.CommandTargetAggregateIdentifier;

public class CreateAccountCommand {

	@CommandTargetAggregateIdentifier
	private final String accountId;

	@ConstructorProperties({ "accountId" })
	public CreateAccountCommand(String accountId) {
		this.accountId = requireNonNull(accountId, () -> "accountId missing");
	}

	public String getAccountId() {
		return accountId;
	}

	@Override
	public String toString() {
		return "CreateAccountCommand [accountId=" + accountId + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		CreateAccountCommand other = (CreateAccountCommand) obj;
		return Objects.equals(accountId, other.accountId);
	}
	
	private static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return obj;
    }
}