package org.project.memospace.application.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.notetype.GetNoteTypeQuery;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
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