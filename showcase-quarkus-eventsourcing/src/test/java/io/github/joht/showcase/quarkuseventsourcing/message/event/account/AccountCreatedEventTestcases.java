package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import io.github.joht.showcase.quarkuseventsourcing.message.event.account.AccountCreatedEvent;

/**
 * Object-Mother for Test-{@link AccountCreatedEvent}s.
 * 
 * @author JohT
 */
enum AccountCreatedEventTestcases {

    ACCOUNT_V1 {
        @Override
        public AccountCreatedEvent build() {

            return new AccountCreatedEvent("1234-56789-0123456789");
        }

        @Override
        public String json() {
            return "{\"accountId\":\"1234-56789-0123456789\"}";
        }
    },

    ;

    public abstract AccountCreatedEvent build();

    public abstract String json();

}
