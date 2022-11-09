package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.FetchDistinctNicknamesQuery;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;

@ApplicationScoped
@Path("/nicknameevents")
public class NicknameEventStreamResource {

    @Inject
    QuerySubmitterService queries;

    @Context
    Sse sse;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getNicknameStream(
            @QueryParam("contains") @DefaultValue("") String partOfNickname,
            @HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") long lastEventId,
            @Context SseEventSink eventSink)
            throws InterruptedException, ExecutionException {

        FetchDistinctNicknamesQuery query = FetchDistinctNicknamesQuery.allNicknamesLike(partOfNickname).usingOffset(lastEventId);
        NicknameEventSubscriber updateNicknames = new NicknameEventSubscriber(sse, eventSink);
        updateNicknames.accept(queryNicknames(query, updateNicknames));
    }

    private List<NicknameDetails> queryNicknames(FetchDistinctNicknamesQuery query, Consumer<List<NicknameDetails>> toBeUpdated) {
        String details = String.format("Could not read nicknames containing %s", query.getPartOfNickname());
        return queries.waitFor(queries.querySubscribedList(query, NicknameDetails.class, toBeUpdated), details);
    }
}