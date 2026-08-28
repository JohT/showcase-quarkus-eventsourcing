package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

class ChangeNicknameCommandTest {

	private static final Nickname NICKNAME = Nickname.of("Nick");
	
	private ChangeNicknameCommand commandUnderTest;

	@Test
	void containsAccountId() {
		String expectedValue = "1234";
		commandUnderTest = createChangeNicknameCommand(expectedValue, NICKNAME);
		assertEquals(expectedValue, commandUnderTest.getAccountId());
	}

	@Test
	void containsNickname() {
		Nickname expectedValue = NICKNAME;
		commandUnderTest = createChangeNicknameCommand("", expectedValue);
		assertEquals(expectedValue, commandUnderTest.getNickname());
	}

	@Test
	void failsOnMissingAccountId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> createChangeNicknameCommand(null, NICKNAME));
		assertEquals("accountId missing", exception.getMessage());
	}

	@Test
	void failsOnMissingNickname() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> createChangeNicknameCommand("", null));
		assertEquals("nickname missing", exception.getMessage());
	}
	
	private ChangeNicknameCommand createChangeNicknameCommand(String accountId, Nickname nickname) {
		return new ChangeNicknameCommand(accountId, nickname);
	}
}