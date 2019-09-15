package io.github.joht.showcase.quarkuseventsourcing.message.query.nickname;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.Objects;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

public class NicknameDetails {

	private final Nickname nickname;
    private final Instant changed;

    @ConstructorProperties({ "nickname", "changed" })
    public NicknameDetails(Nickname nickname, Instant changed) {
		this.nickname = nickname;
        this.changed = changed;
	}

	public Nickname getNickname() {
		return nickname;
	}

	public long getSequenceNumber() {
        return changed.toEpochMilli();
	}

    public Instant getChanged() {
        return changed;
    }

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		NicknameDetails castOther = (NicknameDetails) other;
        return Objects.equals(nickname, castOther.nickname) && Objects.equals(changed, castOther.changed);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nickname);
	}

	@Override
    public String toString() {
        return "NicknameDetails [nickname=" + nickname + ", changed=" + changed + "]";
    }
}