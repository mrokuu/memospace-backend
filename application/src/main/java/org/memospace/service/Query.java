package org.memospace.service;

/**
 * Marker interface for all queries in the CQRS pattern.
 * Queries represent read operations that retrieve data without modifying state.
 */
public interface Query<T> {
}