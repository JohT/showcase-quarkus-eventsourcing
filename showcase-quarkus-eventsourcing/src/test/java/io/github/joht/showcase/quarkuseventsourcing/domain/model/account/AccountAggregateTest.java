package io.github.joht.showcase.quarkuseventsourcing.domain.model.account;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.domain.model.PreconfiguredAggregateTestFixture;
import io.github.joht.showcase.quarkuseventsourcing.domain.model.account.AccountAggregate;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.ChangeNicknameCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.CreateAccountCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknamePresetEvent;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

public class AccountAggregateTest {

    private static final String ID = "1234";

    private AggregateTestFixture<AccountAggregate> accountFixture;

    @BeforeEach
    public void setUp() {
        accountFixture = new PreconfiguredAggregateTestFixture<>(AccountAggregate.class).getFixture();
    }

    @Test
    public void createdAccountPresetsNickname() {
        accountFixture.givenNoPriorActivity().when(new CreateAccountCommand(ID))
                .expectEvents(new AccountCreatedEvent(ID), NicknamePresetEvent.noNicknameFor(ID));
    }

    @Test
    public void nicknameShouldBeChangeable() {
        Nickname nickname = Nickname.of("TestNickname");
        accountFixture.givenCommands(new CreateAccountCommand(ID))
                .when(new ChangeNicknameCommand(ID, nickname))
                .expectEvents(new NicknameChangedEvent(ID, nickname, Nickname.none()));
    }

    @Test
    public void equalsAndHashcodeTechnicallyCorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(AccountAggregate.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("accountId")
                .usingGetClass()
                .report();
        assertTrue(report.isSuccessful(), report.getMessage());
    }
}