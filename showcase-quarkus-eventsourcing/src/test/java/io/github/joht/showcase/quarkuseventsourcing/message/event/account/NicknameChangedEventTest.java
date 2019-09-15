package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;

class NicknameChangedEventTest {

    private static final String ACCOUNT_ID = "123456789";
    private static final Nickname NICKNAME = Nickname.of("The Rock");
    private static final Nickname OLD_NICKNAME = Nickname.of("The Rck");

    private NicknameChangedEvent eventToTest;

    @Test
    @DisplayName("contains the account id")
    void testContainsAccountId() {
        eventToTest = new NicknameChangedEvent(ACCOUNT_ID, NICKNAME, OLD_NICKNAME);
        assertEquals(ACCOUNT_ID, eventToTest.getAccountId());
    }

    @Test
    @DisplayName("contains the nickname")
    void testContainsNickname() {
        eventToTest = new NicknameChangedEvent(ACCOUNT_ID, NICKNAME, OLD_NICKNAME);
        assertEquals(NICKNAME, eventToTest.getNickname());
    }

    @Test
    @DisplayName("contains the old nickname")
    void testContainsOldNickname() {
        eventToTest = new NicknameChangedEvent(ACCOUNT_ID, NICKNAME, OLD_NICKNAME);
        assertEquals(OLD_NICKNAME, eventToTest.getOldNickname());
    }

    @Test
    @DisplayName("the old nickname is none when set to null")
    void testAcceptsOldNicknameNull() {
        eventToTest = new NicknameChangedEvent(ACCOUNT_ID, NICKNAME, null);
        assertEquals(Nickname.none(), eventToTest.getOldNickname());
    }

    @Test
    @DisplayName("serializable")
    void testCasesDeserializable() {
        Jsonb jsonb = JsonbBuilder.create();
        for (NicknameChangedEventTestcases testcase : NicknameChangedEventTestcases.values()) {
            NicknameChangedEvent deserialized = jsonb.fromJson(testcase.json(), NicknameChangedEvent.class);
            assertEquals(testcase.build(), deserialized, testcase.name() + ": " + jsonb.toJson(testcase.build()));
        }
    }

    @Test
    @DisplayName("the account id must not be null")
    void testAccountIdMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new NicknameChangedEvent(null, NICKNAME, OLD_NICKNAME));
    }

    @Test
    @DisplayName("the nickname must not be null")
    void testNicknameMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new NicknameChangedEvent(ACCOUNT_ID, null, OLD_NICKNAME));
    }
}