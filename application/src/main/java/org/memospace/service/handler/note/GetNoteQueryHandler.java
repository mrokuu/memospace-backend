package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.note.GetNoteQuery;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.model.Note;
import org.memospace.port.NoteRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNoteQueryHandler implements QueryHandler<GetNoteQuery, Note> {

    private final NoteRepositoryPort noteRepository;


    @Override
    public Note handle(GetNoteQuery query) {

        return noteRepository.findById(query.getNoteId())
                .orElseThrow(() -> new NoteNotFoundException(query.getNoteId()));
    }

    public List<Note> executeSearch(Long deckId, String tag, String searchQuery, int page, int size) {
        return noteRepository.findAll(deckId, tag, searchQuery, page, size);
    }
}