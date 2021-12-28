package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknamePresetEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.query.account.AccountNicknameQuery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.EventPublishingService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Read-Side (Integration-)Test that operates on message level. 
 * <p>
 * This is a black-box test. Published events represent the input. Query messages return the output,
 * that is compared to the expected values. 
 * Given that there is no pre-existing state, the test is perfectly reconstructible and repeatable.
 * <p>
 * This test also shows the strength of a CQRS-Read-Side (Projection) that ideally only depends 
 * on the events it consumes and its current state. 
 * Test cases can be completely described as a sequence of events.
 */
@QuarkusTest
class AccountProjectionTest {

	@Inject
	EventPublishingService eventGateway;
	
	@Inject
	QuerySubmitterService queryGateway;
	
	@Test
	@DisplayName("After a account had been created and the nickname had been changed the new nickname is returned by the query")
	void nicknameChangeQueryable() throws InterruptedException, ExecutionException {
		String accountId = createTestAccountId();
		Nickname expectedNickname = Nickname.of("Eddie");
		
		eventGateway.publish(new AccountCreatedEvent(accountId));
		eventGateway.publish(new NicknamePresetEvent(accountId, Nickname.none()));
		eventGateway.publish(new NicknameChangedEvent(accountId, expectedNickname, Nickname.none()));
		
		Nickname nickname = queryGateway.query(new AccountNicknameQuery(accountId), Nickname.class).get();
		assertEquals(expectedNickname, nickname);
	}

	private String createTestAccountId() {
		return getClass().getSimpleName() + "-accountId-" + UUID.randomUUID();
	}
}