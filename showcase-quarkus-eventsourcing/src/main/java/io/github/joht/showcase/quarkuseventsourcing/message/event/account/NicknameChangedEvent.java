package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import static io.github.joht.showcase.quarkuseventsourcing.message.event.internal.InternalEventValueAssertion.notNull;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.singletonMap;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.EventRevision;

@EventRevision("1")
public class NicknameChangedEvent {

    public static final String EVENT_NAME = NicknameChangedEvent.class.getName();
    public static final Map<String, Object> OLD_NICKNAME = Collections.singletonMap("value", "");
    public static final Map<String, Object> MISSING_FIELDS_TO_V1 = singletonMap("oldNickname", OLD_NICKNAME);

	private final String accountId;
	private final Nickname nickname;
    private final Nickname oldNickname;

    @ConstructorProperties({ "accountId", "nickname", "oldNickname" })
    public NicknameChangedEvent(String accountId, Nickname nickname, Nickname oldNickname) {
        this.accountId = notNull(accountId, () -> "accountId may not be null");
        this.nickname = notNull(nickname, () -> "nickname may not be null");
        this.oldNickname = (oldNickname != null) ? oldNickname : Nickname.none();
	}

	public String getAccountId() {
		return accountId;
	}

	public Nickname getNickname() {
		return nickname;
	}

    public Nickname getOldNickname() {
        return oldNickname;
    }

	@Override
	public boolean equals(final Object other) {
        if ((other == null) || (!getClass().equals(other.getClass()))) {
			return false;
		}
		NicknameChangedEvent castOther = (NicknameChangedEvent) other;
        return Objects.equals(accountId, castOther.accountId)
                && Objects.equals(nickname, castOther.nickname)
                && Objects.equals(oldNickname, castOther.oldNickname);
	}

	@Override
	public int hashCode() {
        return Objects.hash(accountId, nickname);
	}
	
	@Override
    public String toString() {
        return "NicknameChangedEvent [accountId=" + accountId + ", nickname=" + nickname + ", oldNickname=" + oldNickname + "]";
    }
}