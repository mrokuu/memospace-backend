package org.memospace.common.port;

import org.memospace.common.event.DomainEvent;

import java.util.function.Consumer;

/**
 * Port for publishing and subscribing to domain events.
 * This interface belongs to the domain layer and has no infrastructure dependencies.
 *
 * Implementations can be:
 * - In-memory (synchronous) for simplicity
 * - Outbox pattern (transactional) for reliability
 * - Message queue (async) for scalability
 *
 * The domain layer doesn't care about the implementation details.
 */
public interface DomainEventBus {
    /**
     * Publish a domain event to all registered subscribers.
     * The event is delivered synchronously or asynchronously depending on implementation.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);

    /**
     * Subscribe to domain events of a specific type.
     * The handler will be invoked whenever an event of this type is published.
     *
     * @param eventType the class of events to subscribe to
     * @param handler   the handler to invoke when events are published
     * @param <T>       the event type
     */
    <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler);

    /**
     * Remove all subscribers for a specific event type.
     *
     * @param eventType the class of events to unsubscribe from
     * @param <T>       the event type
     */
    <T extends DomainEvent> void unsubscribe(Class<T> eventType);

    /**
     * Clear all subscribers (useful for testing).
     */
    void clearAllSubscribers();
}
