package io.github.joht.showcase.quarkuseventsourcing.message.command.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateAccountCommandTest {

	private CreateAccountCommand commandUnderTest;
	
	@Test
	void containsAccountId() {
		String expectedValue = "12345";
		commandUnderTest = new CreateAccountCommand(expectedValue);
		assertEquals(expectedValue, commandUnderTest.getAccountId());
	}

	@Test
	void failsOnMissingAccountId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> new CreateAccountCommand(null));
		assertEquals("accountId missing", exception.getMessage());
	}
}
