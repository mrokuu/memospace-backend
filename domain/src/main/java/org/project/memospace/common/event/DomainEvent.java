package org.project.memospace.domain.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 * Domain events represent significant occurrences within the domain that domain experts care about.
 * They are immutable and describe something that has already happened.
 *
 * Events should be named in past tense (e.g., NoteCreated, CardReviewed, DeckDeleted).
 */
public interface DomainEvent {
    /**
     * Unique identifier for this event instance.
     */
    UUID eventId();

    /**
     * Timestamp when the event occurred (in domain time).
     */
    Instant occurredOn();

    /**
     * Type name of the aggregate that published this event.
     * Used for routing and debugging.
     */
    String aggregateType();

    /**
     * ID of the aggregate instance that published this event.
     * Stored as String to accommodate different ID types (UUID, Long, etc.).
     */
    String aggregateId();

    /**
     * Optional user ID who triggered the event (for future multi-tenancy).
     * Returns null if not applicable or not yet implemented.
     */
    default UUID ownerUserId() {
        return null;
    }

    /**
     * Name of this event type for serialization and routing.
     * Default implementation uses simple class name.
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}
