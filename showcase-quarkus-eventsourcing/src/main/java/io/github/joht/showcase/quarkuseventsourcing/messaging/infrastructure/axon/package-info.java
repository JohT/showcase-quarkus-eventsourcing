/**
 * This package contains the Axon configuration, which is also the implementation of the messaging boundary.<br>
 * The sub packages contain the adapters to connect Axon <br>
 * to CDI (for injection), to JTA (for transactions) and to JSON-B / JSON-P (for JSON serialization).<br>
 * Database-specific adapters (PostgreSql JSONB column type support) are also located here.
 * <p>
 * This package and all sub packages should not contain any business related things.<br>
 * These are responsible for the "technical stuff", and should also be the only place for that.
 * <p>
 * Note: To split up command and query side, the contents in this package need further modularization.
 */
package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;