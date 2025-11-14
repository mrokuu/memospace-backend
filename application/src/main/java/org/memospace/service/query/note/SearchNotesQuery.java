package org.memospace.service.query.note;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Note;

import java.util.List;

@Value
public class SearchNotesQuery implements Query<List<Note>> {
    Long deckId;
    String tag;
    String searchQuery;
    int page;
    int size;
}