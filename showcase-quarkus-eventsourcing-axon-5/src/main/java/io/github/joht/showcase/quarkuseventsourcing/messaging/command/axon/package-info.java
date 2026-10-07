/**
 * This package contains the command/messaging "adapters" (see hexagonal architecture).
 * <p>
 * The (business) core of the application must not use these classes directly.<br>
 * The contained adapters implement the boundary/ports-interfaces for axon.
 */
package io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon;