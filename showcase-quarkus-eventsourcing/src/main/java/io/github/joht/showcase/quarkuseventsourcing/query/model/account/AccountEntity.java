package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

@Entity
@Table(name = "account", schema = "axon_on_microprofile_query_tryout")
public class AccountEntity {

	@EmbeddedId
	private AccountEntityKey key;

	@Column(name = "NICKNAME", nullable = false)
	private String nickname = "";

	/**
	 * @deprecated Internal constructor for frameworks. Not meant to be called directly.
	 */
	@Deprecated
	protected AccountEntity() {
		super();
	}

	public AccountEntity(AccountEntityKey key) {
		this.key = key;
	}

	public AccountEntityKey getKey() {
		return key;
	}

	public Nickname getNickname() {
		return Nickname.of(nickname);
	}

	public void setNickname(Nickname nickname) {
		this.nickname = nickname.getValue();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountEntity castOther = (AccountEntity) other;
		return Objects.equals(key, castOther.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "AccountEntity [key=" + key + ", nickname=" + nickname + "]";
	}
}