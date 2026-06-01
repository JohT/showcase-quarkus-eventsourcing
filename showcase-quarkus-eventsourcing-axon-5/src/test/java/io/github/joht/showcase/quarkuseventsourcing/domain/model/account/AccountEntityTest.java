package io.github.joht.showcase.quarkuseventsourcing.domain.model.account;

import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.message.command.account.ChangeNicknameCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.CreateAccountCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknamePresetEvent;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon.AggregateEventEmitterServiceParameterResolverFactory;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;

public class AccountEntityTest {

    private static final String ACCOUNT_ID = "1234";

    private AxonTestFixture fixture;

    @BeforeEach
    public void setUp() {
        EventSourcingConfigurer configurer = EventSourcingConfigurer.create()
                .registerEventStorageEngine(config -> new InMemoryEventStorageEngine())
                .registerEntity(EventSourcedEntityModule.autodetected(String.class, AccountEntity.class))
                .messaging(messagingConfigurer -> messagingConfigurer.registerParameterResolverFactory(
                        configuration -> new AggregateEventEmitterServiceParameterResolverFactory()
                ))
                .componentRegistry(registry -> registry.registerComponent(
                        AggregateEventEmitterService.class,
                        configuration -> new TestAggregateEventEmitterService()
                ));
        fixture = AxonTestFixture.with(configurer);
    }

    @AfterEach
    public void tearDown() {
        fixture.stop();
    }

    @Test
    public void createdAccountPresetsNickname() {
        fixture.given()
                .noPriorActivity()
                .when()
                .command(new CreateAccountCommand(ACCOUNT_ID))
                .then()
                .success()
                .events(new AccountCreatedEvent(ACCOUNT_ID), NicknamePresetEvent.noNicknameFor(ACCOUNT_ID));
    }

    @Test
    public void nicknameShouldBeChangeable() {
        Nickname nickname = Nickname.of("TestNickname");
        fixture.given()
                .commands(new CreateAccountCommand(ACCOUNT_ID))
                .when()
                .command(new ChangeNicknameCommand(ACCOUNT_ID, nickname))
                .then()
                .success()
                .events(new NicknameChangedEvent(ACCOUNT_ID, nickname, Nickname.none()));
    }

    private static class TestAggregateEventEmitterService implements AggregateEventEmitterService {

        @Override
        public void apply(Object event) {
            // Test implementation - events are validated by test fixture expectations
        }
    }
}
