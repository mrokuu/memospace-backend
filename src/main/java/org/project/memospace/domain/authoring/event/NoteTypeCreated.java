package org.project.memospace.domain.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.project.memospace.domain.common.event.BaseDomainEvent;
import org.project.memospace.domain.common.value.NoteTypeId;

/**
 * Domain event published when a new NoteType is created.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NoteTypeCreated extends BaseDomainEvent {
    private final NoteTypeId noteTypeId;
    private final String name;

    public NoteTypeCreated(NoteTypeId noteTypeId, String name) {
        super("NoteType", noteTypeId.toString());
        this.noteTypeId = noteTypeId;
        this.name = name;
    }
}
