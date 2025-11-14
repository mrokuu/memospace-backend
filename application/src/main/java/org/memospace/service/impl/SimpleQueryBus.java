package org.memospace.service.impl;

import org.memospace.service.Query;
import org.memospace.service.QueryBus;
import org.memospace.service.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of QueryBus using in-memory handler registry.
 */
@Component
public class SimpleQueryBus implements QueryBus {

    private final Map<Class<? extends Query<?>>, QueryHandler<? extends Query<?>, ?>> handlers = new HashMap<>();

    /**
     * Register a query handler.
     *
     * @param queryClass The query class
     * @param handler The handler for the query
     */
    public <Q extends Query<R>, R> void registerHandler(Class<Q> queryClass, QueryHandler<Q, R> handler) {
        handlers.put(queryClass, handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R send(Query<R> query) {
        QueryHandler<Query<R>, R> handler = (QueryHandler<Query<R>, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for query: " + query.getClass().getSimpleName());
        }
        return handler.handle(query);
    }
}