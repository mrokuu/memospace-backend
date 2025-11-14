package org.memospace.service;

/**
 * Interface for query handlers in the CQRS pattern.
 * Query handlers process queries and execute read operations.
 *
 * @param <Q> The query type
 * @param <R> The result type
 */
public interface QueryHandler<Q extends Query<R>, R> {

    /**
     * Handle the query and return the result.
     *
     * @param query The query to handle
     * @return The result of the query execution
     */
    R handle(Q query);
}