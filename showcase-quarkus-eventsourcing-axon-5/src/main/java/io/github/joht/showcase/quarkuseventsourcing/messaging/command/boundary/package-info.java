/**
 * This package contains the command/messaging "boundary" aka. "ports" (see hexagonal architecture).
 * <p>
 * The (business) core of the application must not use axon directly for messaging.<br>
 * The (business) core only uses interfaces and classes in this package.<br>
 * These are implemented by adapters, which are responsible to wire them up to axon.
 * <p>
 * This package might contain dependencies to runtime annotations of axon as part of meta-annotation. <br>
 * If this boundary package would be a separate module, these dependencies can be declared as optional.<br>
 * Runtime Annotations, that are missing in the classpath, are just ignored and do not lead to compile errors.
 */
package io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary;