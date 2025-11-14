package org.memospace.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.notetype.GetNoteTypeQuery;
import org.memospace.exception.NoteTypeNotFoundException;
import org.memospace.model.NoteType;
import org.memospace.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNoteTypeQueryHandler implements QueryHandler<GetNoteTypeQuery, NoteType> {

    private final NoteTypeRepositoryPort noteTypeRepository;

    @Override
    public NoteType handle(GetNoteTypeQuery query) {
        return noteTypeRepository.findById(query.getNoteTypeId())
                .orElseThrow(() -> new NoteTypeNotFoundException(query.getNoteTypeId()));
    }

    public List<NoteType> executeGetAll() {
        return noteTypeRepository.findAll();
    }
}