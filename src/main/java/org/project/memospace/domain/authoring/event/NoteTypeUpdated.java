package org.project.memospace.domain.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.project.memospace.domain.common.event.BaseDomainEvent;
import org.project.memospace.domain.common.value.NoteTypeId;

/**
 * Domain event published when a NoteType is updated.
 * May trigger card regeneration for all notes of this type if templates changed.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoteTypeUpdated extends BaseDomainEvent {
    private final NoteTypeId noteTypeId;
    private final boolean templatesChanged;

    public NoteTypeUpdated(NoteTypeId noteTypeId, boolean templatesChanged) {
        super("NoteType", noteTypeId.toString());
        this.noteTypeId = noteTypeId;
        this.templatesChanged = templatesChanged;
    }
}
