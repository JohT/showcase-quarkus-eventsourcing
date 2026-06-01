package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Provides methods to submit queries.
 */
public interface QuerySubmitterService {

    /**
     * Submits the given {@code query}. Expects a response with the given {@code responseType} from a single source.
     *
     * @param query        The {@code query} to be sent
     * @param responseType The response type used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link CompletableFuture} containing the query result
     */
    <R, Q> CompletableFuture<R> query(Q query, Class<R> responseType);

    /**
     * Submits the given {@code query}. Returns the initial result as a {@link CompletableFuture}-{@link List}.
     * Gets notified when the result changes.
     *
     * @param query               The {@code query} to be sent
     * @param responseElementType The response element type used for this query
     * @param resultUpdateAction  {@link Consumer} that gets notified on new results
     * @param <Q>                 The type of the query
     * @param <R>                 The response class
     * @return A {@link CompletableFuture} containing the initial query result
     */
    <Q, R> CompletableFuture<List<R>> querySubscribedList(Q query, Class<R> responseElementType,
            Consumer<? super List<R>> resultUpdateAction);

    /**
     * Supports synchronous queries to wait for the result, including timeout-handling.
     *
     * @param queryResult  {@link CompletableFuture}
     * @param queryDetails {@link String}
     * @return query result
     */
    <R> R waitFor(CompletableFuture<R> queryResult, String queryDetails);
}
