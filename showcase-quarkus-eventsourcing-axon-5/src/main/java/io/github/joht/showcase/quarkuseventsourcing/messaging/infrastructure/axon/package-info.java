/**
 * This package contains the Axon 5 configuration, which is also the implementation of the messaging boundary.<br>
 * The sub packages contain the adapters to connect Axon 5 to CDI (for injection) and to JSON-B (for JSON serialization).<br>
 * <p>
 * Compared to the Axon 4 module, this module uses {@code EventSourcingConfigurer} and {@code InMemoryEventStorageEngine}
 * instead of JPA-backed storage, eliminating the need for a separate command-side database.
 * <p>
 * This package and all sub packages should not contain any business related things.<br>
 * These are responsible for the "technical stuff", and should also be the only place for that.
 */
package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;
