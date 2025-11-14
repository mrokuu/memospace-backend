package org.memospace.service.query.note;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Card;

import java.util.List;
import java.util.UUID;

@Value
public class GetNoteCardsQuery implements Query<List<Card>> {
    UUID noteId;
}