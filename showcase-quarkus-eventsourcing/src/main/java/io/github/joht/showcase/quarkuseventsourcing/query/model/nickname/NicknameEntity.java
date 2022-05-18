package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;

@Entity
@Table(name = "\"nickname\"", schema = "\"axon_on_microprofile_query_tryout\"")
@NamedQueries({
        @NamedQuery(name = NicknameEntity.QUERY_NICKNAMES, query = " SELECT nickname FROM NicknameEntity nickname "
                + "WHERE nickname.key.nickname LIKE :partOfNickname "
                + "  AND nickname.sequenceNumber > :sequenceStart "
                + "ORDER BY nickname.sequenceNumber"),
        @NamedQuery(name = NicknameEntity.DELETE_ALL_NICKNAMES, query = " DELETE FROM NicknameEntity "), })
public class NicknameEntity {

    public static final String QUERY_NICKNAMES = "NicknameEntityQueryNicknames";
    public static final String DELETE_ALL_NICKNAMES = "NicknameEntityDeleteAllNicknames";

    @EmbeddedId
    private NicknameEntityKey key;

    @Column(name = "COUNT")
    private long count = 0;

    @Column(name = "CREATESEQUENCE")
    private long sequenceNumber = 0;

    @Column(name = "FIRSTCHOSEN", nullable = true)
    private Timestamp firstChosen;

    @Column(name = "LASTCHOSEN", nullable = true)
    private Timestamp lastChosen;

    /**
     * @deprecated Internal constructor for frameworks. Not meant to be called directly.
     */
    @Deprecated
    protected NicknameEntity() {
        super();
    }

    public NicknameEntity(NicknameEntityKey key) {
        this.key = key;
    }

    public static NicknameEntity ofNicknameDetails(NicknameDetails nicknameDetails) {
        NicknameEntityKey key = NicknameEntityKey.of(nicknameDetails.getNickname());
        return new NicknameEntity(key).updateNicknameDetails(nicknameDetails);
    }

    public NicknameEntity updateNicknameDetails(NicknameDetails nicknameDetails) {
        incrementCount();
        if (isDistinct()) {
            setSequenceNumber(nicknameDetails.getSequenceNumber());
            setFirstChosen(nicknameDetails.getChanged());
        }
        setLastChosen(nicknameDetails.getChanged());
        return this;
    }

    public NicknameEntityKey getKey() {
        return key;
    }

    public NicknameDetails getNicknameDetails() {
        return new NicknameDetails(getNickname(), getFirstChosen());
    }

    private Nickname getNickname() {
        return new Nickname(key.getNickname());
    }

    private Instant getFirstChosen() {
        return (firstChosen != null) ? firstChosen.toInstant() : Instant.ofEpochMilli(sequenceNumber);
    }

    private void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private void setFirstChosen(Instant instant) {
        this.firstChosen = Timestamp.from(instant);
    }

    private void setLastChosen(Instant instant) {
        this.lastChosen = Timestamp.from(instant);
    }

    public boolean isDistinct() {
        return count <= 1;
    }

    private NicknameEntity incrementCount() {
        count++;
        return this;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        NicknameEntity castOther = (NicknameEntity) other;
        return Objects.equals(key, castOther.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "NicknameEntity [key=" + key + ", count=" + count + ", sequenceNumber=" + sequenceNumber + ", firstChosen=" + firstChosen
                + ", lastChosen=" + lastChosen + "]";
    }
}