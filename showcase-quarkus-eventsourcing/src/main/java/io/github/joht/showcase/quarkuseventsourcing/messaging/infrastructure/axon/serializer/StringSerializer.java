package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer;

public interface StringSerializer {

	/**
	 * Serializes the Java-{@link Object} into a JSON-{@link String}.
	 *
	 * @param object The root object of the object content tree to be serialized.
	 *
	 * @return {@link String} with serialized JSON data.
	 * @throws IllegalArgumentException If any of the parameters is {@code null}.
	 */
	String serialize(Object object);

	/**
	 * Deserializes the JSON-{@link String} back into a new instance of the
	 * Java-{@link Object}.
	 *
	 * @param json The JSON-{@link String} to deserialize.
	 * @param type Type of the resulting Java-{@link Object}.
	 * @param      <T> Type of the resulting Java-{@link Object}.
	 *
	 * @return the newly created Java-{@link Object}
	 * @throws IllegalArgumentException If any of the parameters is {@code null}.
	 */
	<T> T deserialize(String json, Class<T> type);
}