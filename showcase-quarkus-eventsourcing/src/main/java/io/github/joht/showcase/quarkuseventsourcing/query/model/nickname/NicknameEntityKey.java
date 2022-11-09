package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

@Embeddable
public class NicknameEntityKey implements Serializable {

	private static final long serialVersionUID = 6455008100544783181L;

	@Column(name = "NICKNAME", nullable = false)
	private String nickname;

	public static NicknameEntityKey of(Nickname nickname) {
		return new NicknameEntityKey(nickname.getValue());
	}

	/**
	 * @deprecated JPA only
	 */
	@Deprecated
	public NicknameEntityKey() {
		super();
	}
	
	public NicknameEntityKey(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		NicknameEntityKey castOther = (NicknameEntityKey) other;
		return Objects.equals(nickname, castOther.nickname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nickname);
	}

	@Override
	public String toString() {
		return "NicknameEntityKey [nickname=" + nickname + "]";
	}
}