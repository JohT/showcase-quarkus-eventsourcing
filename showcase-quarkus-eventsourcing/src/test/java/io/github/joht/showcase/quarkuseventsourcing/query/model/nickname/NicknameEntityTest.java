package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;
import io.github.joht.showcase.quarkuseventsourcing.query.model.nickname.NicknameEntity;
import io.github.joht.showcase.quarkuseventsourcing.query.model.nickname.NicknameEntityKey;

class NicknameEntityTest {

    private static final Nickname NICKNAME = new Nickname("Test Nickname");
    private static final NicknameEntityKey KEY = new NicknameEntityKey(NICKNAME.getValue());

    NicknameEntity entityToTest = new NicknameEntity(KEY);

    @Test
    @DisplayName("first nickname update sets all nickname details")
    void testContainsUpdatedNicknameDetails() {
        Instant createdInstant = Instant.now();
        NicknameDetails nicknameDetails = new NicknameDetails(NICKNAME, createdInstant);
        entityToTest.updateNicknameDetails(nicknameDetails);
        assertEquals(nicknameDetails, entityToTest.getNicknameDetails());
    }

    @Test
    @DisplayName("second nickname update does not change instant of first creation")
    void testOnlyNicknameChangesOnSecondUpdatedNicknameDetails() {
        Instant createdInstant = Instant.now();
        entityToTest.updateNicknameDetails(new NicknameDetails(Nickname.none(), createdInstant));
        entityToTest.updateNicknameDetails(new NicknameDetails(NICKNAME, Instant.MAX));
        assertEquals(createdInstant, entityToTest.getNicknameDetails().getChanged());
    }
}
