package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;

class AccountCreatedEventTest {

    private static final String ACCOUNT_ID = "12345678901";

    private AccountCreatedEvent eventToTest;

    @Test
    @DisplayName("contains the account id")
    void testContainsAccountId() {
        eventToTest = new AccountCreatedEvent(ACCOUNT_ID);
        assertEquals(ACCOUNT_ID, eventToTest.getAccountId());
    }

    @Test
    @DisplayName("the account id must not be null")
    void testAccountIdMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new AccountCreatedEvent(null));
    }

    @Test
    @DisplayName("serializable")
    void testCasesDeserializable() {
        Jsonb jsonb = JsonbBuilder.create();
        for (AccountCreatedEventTestcases testcase : AccountCreatedEventTestcases.values()) {
            AccountCreatedEvent deserialized = jsonb.fromJson(testcase.json(), AccountCreatedEvent.class);
            assertEquals(testcase.build(), deserialized);
        }
    }
}
