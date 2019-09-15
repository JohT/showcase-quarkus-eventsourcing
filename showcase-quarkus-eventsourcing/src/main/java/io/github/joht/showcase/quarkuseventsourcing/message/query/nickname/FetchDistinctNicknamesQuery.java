package io.github.joht.showcase.quarkuseventsourcing.message.query.nickname;

import java.time.Instant;
import java.util.Objects;

public class FetchDistinctNicknamesQuery {

	private static final String UNFILTERED = "";

	private final String partOfNickname;
    private final Instant createdSince;

    public static final FetchDistinctNicknamesQuery allNicknames() {
        return allNicknamesLike(UNFILTERED);
    }

    public static final FetchDistinctNicknamesQuery allNicknamesLike(String partOfNickname) {
        return new FetchDistinctNicknamesQuery(partOfNickname, Instant.MIN);
    }

    protected FetchDistinctNicknamesQuery(String partOfNickname, Instant createdSince) {
		this.partOfNickname = partOfNickname.replace("%", "\\%").replace("_", "\\_").trim();
        this.createdSince = createdSince;
	}

    public FetchDistinctNicknamesQuery usingOffset(long offset) {
        return createdSince((offset > 0) ? Instant.ofEpochMilli(offset) : Instant.MIN);
    }

    public FetchDistinctNicknamesQuery createdSince(Instant createdSince) {
        return new FetchDistinctNicknamesQuery(partOfNickname, createdSince);
    }

	public String getPartOfNickname() {
		return partOfNickname;
	}

    public Instant getCreatedSince() {
        return createdSince;
    }

	public long getSequenceOffset() {
        return createdSince.toEpochMilli();
	}

    @Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		FetchDistinctNicknamesQuery castOther = (FetchDistinctNicknamesQuery) other;
		return Objects.equals(partOfNickname, castOther.partOfNickname) //
                && Objects.equals(createdSince, castOther.createdSince);
	}

	@Override
	public int hashCode() {
        return Objects.hash(partOfNickname, createdSince);
	}

	@Override
	public String toString() {
        return "FetchNicknamesQuery [partOfNickname=" + partOfNickname + ", offset=" + createdSince + "]";
	}
}