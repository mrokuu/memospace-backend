package org.project.memospace.application.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.note.GetNoteQuery;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.port.NoteRepositoryPort;
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