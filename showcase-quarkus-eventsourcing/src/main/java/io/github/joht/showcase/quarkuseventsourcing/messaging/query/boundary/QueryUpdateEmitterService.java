package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.util.function.Predicate;

/**
 * Boundary for a service, that informs subscription queries about updates.
 * 
 * @author JohT
 */
public interface QueryUpdateEmitterService {

	/**
	 * Informs the subscribed query, that there is an update for the query result, as far as the parameters of the query
	 * match the given predicate for the updates.
	 *
	 * @param queryType the type of the query
	 * @param filter    predicate on query payload used to filter subscription queries
	 * @param update    incremental update
	 * @param           <Q> the type of the query
	 * @param           <U> the type of the update
	 */
	<Q, U> void emit(Class<Q> queryType, Predicate<? super Q> filter, U update);

}
