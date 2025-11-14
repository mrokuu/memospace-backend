package org.memospace.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.memospace.common.event.BaseDomainEvent;
import org.memospace.common.value.NoteTypeId;

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
