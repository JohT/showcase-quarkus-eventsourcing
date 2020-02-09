package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

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