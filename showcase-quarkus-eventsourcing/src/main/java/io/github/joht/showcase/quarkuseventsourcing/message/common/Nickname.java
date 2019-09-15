package io.github.joht.showcase.quarkuseventsourcing.message.common;

import static io.github.joht.showcase.quarkuseventsourcing.message.event.internal.InternalEventValueAssertion.notNull;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class Nickname {

	private static final Nickname NONE = new Nickname("");

	private final String value;

	@ConstructorProperties({ "value" })
	public Nickname(String value) {
		this.value = notNull(value,  () -> "nickname may not be null");;
	}

    public static final Nickname of(String nickname) {
        return (nickname != null) ? new Nickname(nickname) : none();
	}

	public static final Nickname none() {
		return NONE;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Matches, if the nickname contains the specified sequence of char values.
	 *
	 * @param part the sequence to search for
	 * @return <code>true</code> if it matches
	 */
	public boolean contains(CharSequence part) {
		return value.contains(part);
	}

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        Nickname castOther = (Nickname) other;
        return Objects.equals(value, castOther.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Nickname [value=" + value + "]";
    }
}
