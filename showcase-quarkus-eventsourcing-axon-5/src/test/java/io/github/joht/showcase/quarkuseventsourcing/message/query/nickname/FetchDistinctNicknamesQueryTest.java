package io.github.joht.showcase.quarkuseventsourcing.message.query.nickname;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FetchDistinctNicknamesQueryTest {

    FetchDistinctNicknamesQuery queryToTest;

    @Test
    @DisplayName("contains part of nickname")
    void containsPartOfNickname() {
        String partOfNickname = "Max";
        queryToTest = FetchDistinctNicknamesQuery.allNicknamesLike(partOfNickname);
        assertEquals(partOfNickname, queryToTest.getPartOfNickname());
    }

    @Test
    @DisplayName("database wildscards '%' and '_' get escapted")
    void wildcardsGetEscaped() {
        queryToTest = FetchDistinctNicknamesQuery.allNicknamesLike("Max_%");
        assertEquals("Max\\_\\%", queryToTest.getPartOfNickname());
    }

    @Test
    @DisplayName("a negative offset queries all nicknames ignoring their creation timestamp")
    void negativeOffsetMeansIgnoreCreatedSince() {
        queryToTest = FetchDistinctNicknamesQuery.allNicknamesLike("Max").usingOffset(-1);
        assertEquals(Instant.ofEpochMilli(0), queryToTest.getCreatedSince());
    }

    @Test
    @DisplayName("the offset is taken from the epoch milliseconds of the creation timestamp")
    void offsetIsUsedAsEpochMillies() {
        long offset = Instant.now().toEpochMilli();
        queryToTest = FetchDistinctNicknamesQuery.allNicknamesLike("Max").usingOffset(offset);
        assertEquals(Instant.ofEpochMilli(offset), queryToTest.getCreatedSince());
        assertEquals(offset, queryToTest.getSequenceOffset());
    }
}
