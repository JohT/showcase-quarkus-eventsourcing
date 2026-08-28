package io.github.joht.showcase.quarkuseventsourcing.message.query.nickname;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;

class NicknameDetailsTest {

    NicknameDetails detailsToTest;

    @Test
    @DisplayName("contains nickname")
    void testContainsNickname() {
        Nickname nickname = new Nickname("The Rock");
        detailsToTest = new NicknameDetails(nickname, Instant.now());
        assertEquals(nickname, detailsToTest.getNickname());
    }

    @Test
    @DisplayName("contains changed (instant)")
    void testContainChanged() {
        Instant expectedChanged = Instant.now();
        detailsToTest = new NicknameDetails(new Nickname("The Rock"), expectedChanged);
        assertEquals(expectedChanged, detailsToTest.getChanged());
    }

    @Test
    @DisplayName("contains offset as epoch milliseconds")
    void testContainsSequenceNumber() {
        long expectedSequenceNumber = Instant.now().toEpochMilli();
        detailsToTest = new NicknameDetails(new Nickname("The Rock"), Instant.ofEpochMilli(expectedSequenceNumber));
        assertEquals(expectedSequenceNumber, detailsToTest.getSequenceNumber());
    }
}
