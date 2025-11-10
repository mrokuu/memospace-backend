package org.project.memospace.domain.authoring.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.project.memospace.domain.common.event.BaseDomainEvent;
import org.project.memospace.domain.common.value.NoteId;

/**
 * Domain event published when cards are regenerated from a Note.
 * This happens when note fields change or templates are updated.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CardsRegenerated extends BaseDomainEvent {
    private final NoteId noteId;
    private final int cardCount;

    public CardsRegenerated(NoteId noteId, int cardCount) {
        super("Note", noteId.toString());
        this.noteId = noteId;
        this.cardCount = cardCount;
    }
}
