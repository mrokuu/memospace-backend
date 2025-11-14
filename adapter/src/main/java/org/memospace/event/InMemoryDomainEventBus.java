package org.memospace.event;

import lombok.extern.slf4j.Slf4j;
import org.memospace.common.event.DomainEvent;
import org.memospace.common.port.DomainEventBus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-memory implementation of DomainEventBus for synchronous event delivery.
 * Events are delivered immediately and synchronously within the same thread/transaction.
 * <p>
 * This implementation is simple and suitable for:
 * - Development and testing
 * - Single-instance deployments
 * - Use cases where eventual consistency is not required
 * <p>
 * For production multi-instance deployments, consider:
 * - Outbox pattern with polling publisher
 * - Message queue integration (Kafka, RabbitMQ, etc.)
 */
@Slf4j
@Component
public class InMemoryDomainEventBus implements DomainEventBus {
    private final Map<Class<? extends DomainEvent>, List<Consumer<? extends DomainEvent>>> subscribers =
            new ConcurrentHashMap<>();

    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            log.warn("Attempted to publish null event");
            return;
        }

        log.debug("Publishing event: {} (ID: {}, Aggregate: {}@{})",
                event.eventType(),
                event.eventId(),
                event.aggregateType(),
                event.aggregateId());

        Class<? extends DomainEvent> eventClass = event.getClass();
        List<Consumer<? extends DomainEvent>> handlers = subscribers.get(eventClass);

        if (handlers == null || handlers.isEmpty()) {
            log.trace("No subscribers for event type: {}", eventClass.getSimpleName());
            return;
        }

        for (Consumer<? extends DomainEvent> handler : handlers) {
            try {
                @SuppressWarnings("unchecked")
                Consumer<DomainEvent> typedHandler = (Consumer<DomainEvent>) handler;
                typedHandler.accept(event);
            } catch (Exception e) {
                log.error("Error handling event {} with ID {}: {}",
                        event.eventType(),
                        event.eventId(),
                        e.getMessage(),
                        e);
                // Continue processing other handlers despite this failure
            }
        }
    }

    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null || handler == null) {
            throw new IllegalArgumentException("Event type and handler cannot be null");
        }

        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        log.debug("Subscribed handler to event type: {}", eventType.getSimpleName());
    }

    @Override
    public <T extends DomainEvent> void unsubscribe(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        subscribers.remove(eventType);
        log.debug("Unsubscribed all handlers from event type: {}", eventType.getSimpleName());
    }

    @Override
    public void clearAllSubscribers() {
        int count = subscribers.size();
        subscribers.clear();
        log.debug("Cleared all {} event subscriptions", count);
    }

}
