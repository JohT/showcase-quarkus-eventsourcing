package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import java.beans.ConstructorProperties;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

public class NicknamePresetEvent extends NicknameChangedEvent {

	public static final NicknamePresetEvent noNicknameFor(String accountId) {
		return new NicknamePresetEvent(accountId, Nickname.none());
	}

	@ConstructorProperties({ "accountId", "nickname" })
    public NicknamePresetEvent(String accountId, Nickname nickname) {
        super(accountId, nickname, Nickname.none());
	}
}