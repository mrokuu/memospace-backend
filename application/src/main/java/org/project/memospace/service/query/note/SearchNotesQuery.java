package org.project.memospace.application.service.query.note;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Note;

import java.util.List;

@Value
public class SearchNotesQuery implements Query<List<Note>> {
    Long deckId;
    String tag;
    String searchQuery;
    int page;
    int size;
}