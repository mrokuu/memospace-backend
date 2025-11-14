package org.memospace.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.memospace.common.event.BaseDomainEvent;
import org.memospace.common.value.NoteId;

/**
 * Domain event published when a Note is deleted.
 * Triggers cascade deletion of associated cards.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoteDeleted extends BaseDomainEvent {
    private final NoteId noteId;

    public NoteDeleted(NoteId noteId) {
        super("Note", noteId.toString());
        this.noteId = noteId;
    }
}
