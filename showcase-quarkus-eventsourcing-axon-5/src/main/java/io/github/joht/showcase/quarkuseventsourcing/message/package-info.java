/**
 * This package contains all the message types (mostly value objects). <br>
 * It can bee seen as the "common language". It could also be seen as the "Core API".
 * <p>
 * The contents of this package should no depend on each other (except the "common" package).<br>
 * They should also not depend on anything else. <br>
 * They should be designed to be sharable, even if sharing (especially outside the bounded context) is not advisable.
 */
package io.github.joht.showcase.quarkuseventsourcing.message;