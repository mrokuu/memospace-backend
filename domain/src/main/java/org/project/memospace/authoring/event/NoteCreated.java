package org.project.memospace.domain.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.project.memospace.domain.common.event.BaseDomainEvent;
import org.project.memospace.domain.common.value.DeckId;
import org.project.memospace.domain.common.value.NoteId;
import org.project.memospace.domain.common.value.NoteTypeId;

/**
 * Domain event published when a new Note is created.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoteCreated extends BaseDomainEvent {
    private final NoteId noteId;
    private final NoteTypeId noteTypeId;
    private final DeckId deckId;

    public NoteCreated(NoteId noteId, NoteTypeId noteTypeId, DeckId deckId) {
        super("Note", noteId.toString());
        this.noteId = noteId;
        this.noteTypeId = noteTypeId;
        this.deckId = deckId;
    }
}
