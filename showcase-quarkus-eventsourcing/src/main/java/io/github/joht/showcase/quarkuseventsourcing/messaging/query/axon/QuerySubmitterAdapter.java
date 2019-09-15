package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;
import reactor.core.publisher.Flux;
import reactor.util.Loggers;

public class QuerySubmitterAdapter implements QuerySubmitterService {

    private static final reactor.util.Logger REACTIVE_LOGGER = Loggers.getLogger(QuerySubmitterAdapter.class.getName());

    private QueryGateway queryGateway;

    public QuerySubmitterAdapter(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public <R, Q> CompletableFuture<R> query(Q query, Class<R> responseType) {
        return queryGateway.query(query, responseType);
    }

    @Override
    public <Q, R> CompletableFuture<List<R>> querySubscribedList(Q query, Class<R> responseElementType,
            Consumer<? super List<R>> resultUpdateAction) {
        SubscriptionQueryResult<List<R>, R> fetchQuery = queryGateway.subscriptionQuery(query,
                ResponseTypes.multipleInstancesOf(responseElementType), //
                ResponseTypes.instanceOf(responseElementType));
        Flux<List<R>> updates = fetchQuery.updates().log(REACTIVE_LOGGER, Level.FINEST, false).buffer(Duration.ofMillis(125));
        updates.subscribe(resultUpdateAction);
        return fetchQuery.initialResult().toFuture();
    }

    @Override
    public <R> R waitFor(CompletableFuture<R> queryResult, String queryDetails) {
        String details = String.format("Query %s failed", queryDetails);
        return SynchonousQueryResult.of(queryResult).waitAndGet(details);
    }

    @Override
    public String toString() {
        return "QuerySubmitterAdapter [queryGateway=" + queryGateway + "]";
    }
}