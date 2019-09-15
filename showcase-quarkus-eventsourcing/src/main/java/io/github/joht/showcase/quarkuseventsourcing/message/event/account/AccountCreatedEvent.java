package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import static io.github.joht.showcase.quarkuseventsourcing.message.event.internal.InternalEventValueAssertion.notNull;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class AccountCreatedEvent {

    private final String accountId;

	@ConstructorProperties({ "accountId" })
	public AccountCreatedEvent(String accountId) {
		this.accountId = notNull(accountId,  () -> "accountId may not be null");
    }

	public String getAccountId() {
        return accountId;
    }

    @Override
    public boolean equals(final Object other) {
        if ((other == null) || (!getClass().equals(other.getClass()))) {
            return false;
        }
        return Objects.equals(accountId, ((AccountCreatedEvent) other).accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }

    @Override
    public String toString() {
        return "AccountCreatedEvent [accountId=" + accountId + "]";
    }
}