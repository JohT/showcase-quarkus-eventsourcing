package io.github.joht.showcase.quarkuseventsourcing.domain.model.account;

import java.util.Objects;

import io.github.joht.showcase.quarkuseventsourcing.message.command.account.ChangeNicknameCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.CreateAccountCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknamePresetEvent;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelAggregate;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelCommandHandler;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelEventSourcingHandler;

import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.eventsourcing.annotation.reflection.InjectEntityId;

@CommandModelAggregate(type = "Account")
public class AccountEntity {

    private String accountId;

    private Nickname nickname;

    @EntityCreator
    public AccountEntity(@InjectEntityId String accountId) {
        this.accountId = accountId;
    }

    @CommandModelCommandHandler
    public void createWith(CreateAccountCommand command, AggregateEventEmitterService eventEmitter) {
        eventEmitter.apply(new AccountCreatedEvent(command.getAccountId()));
        eventEmitter.apply(NicknamePresetEvent.noNicknameFor(command.getAccountId()));
    }

    @CommandModelCommandHandler
    public void changeNickname(ChangeNicknameCommand command, AggregateEventEmitterService eventEmitter) {
        if (!Objects.equals(nickname, command.getNickname())) {
            eventEmitter.apply(new NicknameChangedEvent(accountId, command.getNickname(), nickname));
        }
    }

    @CommandModelEventSourcingHandler
    private void on(AccountCreatedEvent event) {
        // accountId already set by @EntityCreator constructor injection
    }

    @CommandModelEventSourcingHandler
    private void on(NicknameChangedEvent event) {
        this.nickname = event.getNickname();
    }

    public Nickname getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        return Objects.equals(accountId, ((AccountEntity) other).accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }

    @Override
    public String toString() {
        return "AccountEntity [accountId=" + accountId + ", nickname=" + nickname + "]";
    }
}
