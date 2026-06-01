package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.axonframework.messaging.queryhandling.gateway.QueryGateway;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;

public class QuerySubmitterAdapter implements QuerySubmitterService {

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
        queryGateway.subscriptionQuery(query, responseElementType).subscribe(new Subscriber<R>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(R update) {
                resultUpdateAction.accept(Arrays.asList(update));
            }

            @Override
            public void onError(Throwable error) {
                // subscription update errors are non-fatal
            }

            @Override
            public void onComplete() {
                // no action needed on completion
            }
        });
        return queryGateway.queryMany(query, responseElementType);
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
