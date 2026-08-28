/**
 * This package contains the query/messaging "adapters" (see hexagonal architecture).
 * <p>
 * The (business) core of the application must not use these classes directly.<br>
 * The contained adapters implement the boundary/ports-interfaces for axon.
 */
package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;