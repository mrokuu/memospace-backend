package org.project.memospace.domain.common.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for domain events providing common metadata.
 * Concrete event classes should extend this and add their specific payload.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseDomainEvent implements DomainEvent {
    @EqualsAndHashCode.Include
    private final UUID eventId;
    private final Instant occurredOn;
    private final String aggregateType;
    private final String aggregateId;
    private final UUID ownerUserId;

    protected BaseDomainEvent(String aggregateType, String aggregateId, UUID ownerUserId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.aggregateType = Objects.requireNonNull(aggregateType, "Aggregate type cannot be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.ownerUserId = ownerUserId; // nullable for now
    }

    protected BaseDomainEvent(String aggregateType, String aggregateId) {
        this(aggregateType, aggregateId, null);
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public String aggregateType() {
        return aggregateType;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public UUID ownerUserId() {
        return ownerUserId;
    }
}
