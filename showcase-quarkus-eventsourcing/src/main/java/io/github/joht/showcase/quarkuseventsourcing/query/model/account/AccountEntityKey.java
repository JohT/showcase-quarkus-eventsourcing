package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AccountEntityKey implements Serializable {

	private static final long serialVersionUID = -1768574708066786705L;

	@Column(name = "ACCOUNTID", nullable = false)
	private String accountId;
	
	/**
	 * @deprecated JPA only
	 */
	@Deprecated
	public AccountEntityKey() {
		super();
	}
	
	public AccountEntityKey(String accountId) {
		this.accountId = accountId;
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
		AccountEntityKey castOther = (AccountEntityKey) other;
		return Objects.equals(accountId, castOther.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}

	@Override
	public String toString() {
		return "AccountEntityKey [accountId=" + accountId + "]";
	}
}