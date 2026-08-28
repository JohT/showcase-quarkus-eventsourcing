package io.github.joht.showcase.quarkuseventsourcing.message.event.account;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;

/**
 * Object-Mother for Test-{@link NicknameChangedEvent}s.
 * 
 * @author JohT
 */
enum NicknameChangedEventTestcases {

    NICKNAME_WITHOUT_OLD_ONE {
        @Override
        public NicknameChangedEvent build() {

            return new NicknameChangedEvent("1234-56789-0123456789", Nickname.of("MyNickname"), null);
        }

        @Override
        public String json() {
            return "{\"accountId\":\"1234-56789-0123456789\",\"nickname\":{\"value\":\"MyNickname\"},\"oldNickname\":{\"value\":\"\"}}";
        }
    },
    NICKNAME_COMPLETE {
        @Override
        public NicknameChangedEvent build() {

            return new NicknameChangedEvent("1234-56789-0123456789", Nickname.of("MyNickname"), Nickname.of("MyOldNickname"));
        }

        @Override
        public String json() {
            return "{\"accountId\":\"1234-56789-0123456789\",\"nickname\":{\"value\":\"MyNickname\"},\"oldNickname\":{\"value\":\"MyOldNickname\"}}";
        }
    },

    ;

    public abstract NicknameChangedEvent build();

    public abstract String json();
}
