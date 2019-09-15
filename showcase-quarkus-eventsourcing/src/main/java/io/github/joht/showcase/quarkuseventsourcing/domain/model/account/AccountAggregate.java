package io.github.joht.showcase.quarkuseventsourcing.domain.model.account;

import java.beans.ConstructorProperties;
import java.util.Objects;

import io.github.joht.showcase.quarkuseventsourcing.message.command.account.ChangeNicknameCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.CreateAccountCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknamePresetEvent;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.AggregateEventEmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelAggregate;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelAggregateIdentifier;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelCommandHandler;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandModelEventSourcingHandler;

@CommandModelAggregate(type = "Account")
public class AccountAggregate {
	
	@CommandModelAggregateIdentifier
	private String accountId;

	private Nickname nickname;

	/**
	 * @deprecated Only for frameworks. Not meant to be called directly.
	 */
	@Deprecated
    AccountAggregate() {
		super();
	}

    @ConstructorProperties({ "accountId" })
    public AccountAggregate(String accountId) {
        this.accountId = accountId;
    }

	@CommandModelCommandHandler
    public static final AccountAggregate createWith(CreateAccountCommand command, AggregateEventEmitterService eventService) {
		AccountAggregate newAggregate = new AccountAggregate();
		newAggregate.accountId = command.getAccountId();
		eventService.apply(new AccountCreatedEvent(command.getAccountId()));
		eventService.apply(NicknamePresetEvent.noNicknameFor(command.getAccountId()));
		return newAggregate;
	}

	@CommandModelCommandHandler
	public void changeNickname(ChangeNicknameCommand command, AggregateEventEmitterService eventService) {
		if (!Objects.equals(getNickname(), command.getNickname())) {
            eventService.apply(new NicknameChangedEvent(accountId, command.getNickname(), getNickname()));
		}
	}

	@CommandModelEventSourcingHandler
	private void on(AccountCreatedEvent event) {
		this.accountId = event.getAccountId();
	}

	@CommandModelEventSourcingHandler
	private void on(NicknameChangedEvent event) {
		this.nickname = event.getNickname();
	}

	public String getAccountId() {
		return accountId;
	}

	public Nickname getNickname() {
		return nickname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountAggregate castOther = (AccountAggregate) other;
		return Objects.equals(accountId, castOther.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}

	@Override
	public String toString() {
		return "AccountAggregate [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}