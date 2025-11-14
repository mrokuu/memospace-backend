package org.memospace.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.memospace.common.event.BaseDomainEvent;
import org.memospace.common.value.NoteId;

/**
 * Domain event published when a Note is updated.
 * Triggers card regeneration if field values changed.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoteUpdated extends BaseDomainEvent {
    private final NoteId noteId;
    private final boolean fieldsChanged;

    public NoteUpdated(NoteId noteId, boolean fieldsChanged) {
        super("Note", noteId.toString());
        this.noteId = noteId;
        this.fieldsChanged = fieldsChanged;
    }
}
