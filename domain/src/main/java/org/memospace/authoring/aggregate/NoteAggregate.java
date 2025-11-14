package org.memospace.authoring.aggregate;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.memospace.authoring.event.NoteCreated;
import org.memospace.authoring.event.NoteDeleted;
import org.memospace.authoring.event.NoteUpdated;
import org.memospace.authoring.value.FieldName;
import org.memospace.authoring.value.NoteFields;
import org.memospace.common.event.DomainEvent;
import org.memospace.common.value.AuditTrail;
import org.memospace.common.value.DeckId;
import org.memospace.common.value.NoteId;
import org.memospace.common.value.NoteTypeId;
import org.memospace.common.value.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * NoteAggregate is the aggregate root for the Note concept in the Authoring context.
 * A Note contains structured content (fields) based on a NoteType template.
 * Cards are generated from Notes based on CardTemplates.
 *
 * Invariants:
 * - All field names must be non-empty and <= 64 chars
 * - All field values must be <= 16KB
 * - Tags must be normalized (lowercase, trimmed, non-empty, <= 64 chars)
 * - Note belongs to exactly one Deck
 * - Note has exactly one NoteType
 *
 * This aggregate is responsible for:
 * - Validating field data
 * - Normalizing and validating tags
 * - Publishing domain events on state changes
 * - Enforcing business invariants
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NoteAggregate {
    @EqualsAndHashCode.Include
    private final NoteId id;
    private final NoteTypeId noteTypeId;
    private DeckId deckId;
    private NoteFields fields;
    /**
     * -- GETTER --
     *  Get an immutable copy of tags.
     */ // already immutable
    private Set<Tag> tags;
    private final AuditTrail audit;

    @Getter(AccessLevel.PRIVATE)
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Private constructor for rebuilding from repository
    private NoteAggregate(NoteId id, NoteTypeId noteTypeId, DeckId deckId,
                          NoteFields fields, Set<Tag> tags, AuditTrail audit) {
        this.id = Objects.requireNonNull(id, "Note ID cannot be null");
        this.noteTypeId = Objects.requireNonNull(noteTypeId, "NoteType ID cannot be null");
        this.deckId = Objects.requireNonNull(deckId, "Deck ID cannot be null");
        this.fields = Objects.requireNonNull(fields, "Fields cannot be null");
        this.tags = Collections.unmodifiableSet(new HashSet<>(
                Objects.requireNonNull(tags, "Tags cannot be null")
        ));
        this.audit = Objects.requireNonNull(audit, "Audit trail cannot be null");

        validateFields();
        validateTags();
    }

    /**
     * Factory method: Create a new Note aggregate.
     * Publishes NoteCreated event.
     */
    public static NoteAggregate create(
            NoteTypeId noteTypeId,
            DeckId deckId,
            NoteFields fields,
            Set<Tag> tags
    ) {
        NoteId noteId = NoteId.generate();
        NoteAggregate note = new NoteAggregate(
                noteId,
                noteTypeId,
                deckId,
                fields,
                tags,
                AuditTrail.now()
        );

        note.registerEvent(new NoteCreated(noteId, noteTypeId, deckId));
        return note;
    }

    /**
     * Factory method: Reconstitute aggregate from repository.
     * No events are published.
     */
    public static NoteAggregate reconstitute(
            NoteId id,
            NoteTypeId noteTypeId,
            DeckId deckId,
            NoteFields fields,
            Set<Tag> tags,
            AuditTrail audit
    ) {
        return new NoteAggregate(id, noteTypeId, deckId, fields, tags, audit);
    }

    /**
     * Update the note's content.
     * Publishes NoteUpdated event if fields changed.
     */
    public void update(DeckId newDeckId, NoteFields newFields, Set<Tag> newTags) {
        Objects.requireNonNull(newDeckId, "Deck ID cannot be null");
        Objects.requireNonNull(newFields, "Fields cannot be null");
        Objects.requireNonNull(newTags, "Tags cannot be null");

        boolean fieldsChanged = !this.fields.equals(newFields);

        this.deckId = newDeckId;
        this.fields = newFields;
        this.tags = Set.copyOf(newTags);

        validateFields();
        validateTags();

        registerEvent(new NoteUpdated(this.id, fieldsChanged));
    }

    /**
     * Mark this note as deleted.
     * Publishes NoteDeleted event.
     */
    public void delete() {
        registerEvent(new NoteDeleted(this.id));
    }

    /**
     * Validate that fields are in an allowed state based on NoteType schema.
     * This method validates field structure; schema matching must be done by a domain service.
     */
    private void validateFields() {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Note must have at least one field");
        }

        for (FieldName fieldName : fields.fieldNames()) {
            if (fieldName == null) {
                throw new IllegalArgumentException("Field name cannot be null");
            }
        }
    }

    /**
     * Validate tags are properly normalized.
     * Tag value objects already enforce constraints, so this is a sanity check.
     */
    private void validateTags() {
        for (Tag tag : tags) {
            if (tag == null) {
                throw new IllegalArgumentException("Tag cannot be null");
            }
        }
    }

    /**
     * Check if this note has a specific tag.
     */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * Get all domain events and clear the internal list.
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
