package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import java.time.Instant;
import java.util.function.Predicate;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import io.github.joht.showcase.quarkuseventsourcing.message.event.account.NicknameChangedEvent;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.FetchDistinctNicknamesQuery;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.EventTimestampParameter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelEventHandler;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelResetHandler;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryUpdateEmitterService;

@QueryModelProjection(processingGroup = NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES)
@Transactional(TxType.REQUIRED)
public class NicknameProjection {

    public static final String PROCESSING_GROUP_FOR_NICKNAMES = "query.model.nickname";

    @Inject
    NicknameRepository repository;

    @Inject
    QueryUpdateEmitterService queryUpdateEmitter;

    @QueryModelEventHandler
    void onNicknameChanged(NicknameChangedEvent event, @EventTimestampParameter Instant eventInstant) {
        NicknameDetails details = new NicknameDetails(event.getNickname(), eventInstant);
        boolean distinct = repository.updateNicknameDetails(details);
        queryUpdateEmitter.emit(FetchDistinctNicknamesQuery.class, eventMatchesQuery(details).and(nickname -> distinct), details);
    }

    private static Predicate<? super FetchDistinctNicknamesQuery> eventMatchesQuery(NicknameDetails details) {
        return query -> details.getNickname().contains(query.getPartOfNickname()) && (details.getSequenceNumber() > query.getSequenceOffset());
    }

    @QueryModelResetHandler
    public void onReset() {
        repository.deleteAll();
    }
}
