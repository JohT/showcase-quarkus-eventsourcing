package io.github.joht.showcase.quarkuseventsourcing.message.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

class NicknameTest {

    private Nickname nicknameToTest;

    @Test
    @DisplayName("contains the given value")
    void testContainsTheGivenValue() {
        String expectedValue = "Test Nickname";
        nicknameToTest = new Nickname(expectedValue);
        assertEquals(expectedValue, nicknameToTest.getValue());
    }

    @Test
    @DisplayName("none is sees as nickname of an empty string")
    void testNoneIsEmpty() {
        nicknameToTest = Nickname.none();
        assertEquals("", nicknameToTest.getValue());
    }

    @Test
    @DisplayName("'of' creates a new nickname with the given value")
    void testOfCreatesNickname() {
        String expectedValue = "Test Nickname";
        nicknameToTest = Nickname.of(expectedValue);
        assertEquals(expectedValue, nicknameToTest.getValue());
    }

    @Test
    @DisplayName("'of' creates an empty nickname, if the given value is null")
    void testOfCreatesEmptyNicknameForNull() {
        nicknameToTest = Nickname.of(null);
        assertEquals(Nickname.none(), nicknameToTest);
    }

    @Test
    @DisplayName("nickname contains an expected part")
    void testContainsAGivenPart() {
        String expectedPart = "Nickname";
        nicknameToTest = Nickname.of("Test " + expectedPart + " and so on");
        assertTrue(nicknameToTest.contains(expectedPart));
    }

    @Test
    @DisplayName("nickname does not contain an expected part")
    void testDoesntContainsAGivenPart() {
        String unexpectedPart = "Nickname";
        nicknameToTest = Nickname.of("Test");
        assertFalse(nicknameToTest.contains(unexpectedPart));
    }

    @Test
    @DisplayName("an exception is thrown, if the nickname value is null")
    void testExceptionIfNicknameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Nickname(null));
    }
}