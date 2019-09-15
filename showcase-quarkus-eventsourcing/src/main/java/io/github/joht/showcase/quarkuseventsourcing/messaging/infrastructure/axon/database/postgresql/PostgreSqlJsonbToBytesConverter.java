package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.postgresql;

import org.axonframework.serialization.ContentTypeConverter;
import org.postgresql.util.PGobject;

public class PostgreSqlJsonbToBytesConverter implements ContentTypeConverter<PGobject, byte[]> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<PGobject> expectedSourceType() {
		return PGobject.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<byte[]> targetType() {
		return byte[].class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] convert(PGobject original) {
		if ((original == null) || (original.getValue() == null)) {
			return new byte[0];
		}
		return original.getValue().getBytes();
	}
}