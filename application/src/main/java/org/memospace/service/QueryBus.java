package org.memospace.service;

/**
 * Query bus interface for the CQRS pattern.
 * Routes queries to their appropriate handlers.
 */
public interface QueryBus {

    /**
     * Send a query to its handler.
     *
     * @param query The query to send
     * @param <R> The result type
     * @return The result of the query execution
     */
    <R> R send(Query<R> query);
}