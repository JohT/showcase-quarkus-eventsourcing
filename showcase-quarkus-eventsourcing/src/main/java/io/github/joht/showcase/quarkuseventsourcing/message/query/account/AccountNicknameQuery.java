package io.github.joht.showcase.quarkuseventsourcing.message.query.account;

import java.util.Objects;

public class AccountNicknameQuery {

	private final String accountId;

	public static final AccountNicknameQuery queryNicknameForAccount(String accountId) {
		return new AccountNicknameQuery(accountId);
	}

	protected AccountNicknameQuery(String accountId) {
		this.accountId = accountId.trim();
	}

	public String getAccountId() {
		return accountId;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountNicknameQuery castOther = (AccountNicknameQuery) other;
		return Objects.equals(accountId, castOther.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}
	
	@Override
	public String toString() {
		return "AccountNicknameQuery [accountId=" + accountId + "]";
	}
}