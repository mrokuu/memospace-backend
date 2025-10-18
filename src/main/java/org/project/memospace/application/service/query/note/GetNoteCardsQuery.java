package org.project.memospace.application.service.query.note;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Card;

import java.util.List;
import java.util.UUID;

@Value
public class GetNoteCardsQuery implements Query<List<Card>> {
    UUID noteId;
}