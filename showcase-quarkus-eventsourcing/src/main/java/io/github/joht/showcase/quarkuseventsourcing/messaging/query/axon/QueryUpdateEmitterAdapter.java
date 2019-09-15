package io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon;

import java.util.function.Predicate;

import org.axonframework.queryhandling.QueryUpdateEmitter;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryUpdateEmitterService;

public class QueryUpdateEmitterAdapter implements QueryUpdateEmitterService {

    private QueryUpdateEmitter queryUpdateEmitter;

    public QueryUpdateEmitterAdapter(QueryUpdateEmitter queryUpdateEmitter) {
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @Override
    public <Q, U> void emit(Class<Q> queryType, Predicate<? super Q> filter, U update) {
        queryUpdateEmitter.emit(queryType, filter, update);
    }

    @Override
    public String toString() {
        return "QueryUpdateEmitterAdapter [queryUpdateEmitter=" + queryUpdateEmitter + "]";
    }
}