package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.note.SearchNotesQuery;
import org.memospace.model.Note;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchNotesQueryHandler implements QueryHandler<SearchNotesQuery, List<Note>> {

    private final GetNoteQueryHandler getNoteQueryHandler;

    @Override
    public List<Note> handle(SearchNotesQuery query) {
        return getNoteQueryHandler.executeSearch(
                query.getDeckId(),
                query.getTag(),
                query.getSearchQuery(),
                query.getPage(),
                query.getSize()
        );
    }
}