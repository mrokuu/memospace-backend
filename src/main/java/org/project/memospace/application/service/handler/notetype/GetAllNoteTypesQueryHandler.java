package org.project.memospace.application.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.notetype.GetAllNoteTypesQuery;
import org.project.memospace.domain.model.NoteType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllNoteTypesQueryHandler implements QueryHandler<GetAllNoteTypesQuery, List<NoteType>> {

    private final GetNoteTypeQueryHandler getNoteTypeQueryHandler;

    @Override
    public List<NoteType> handle(GetAllNoteTypesQuery query) {
        return getNoteTypeQueryHandler.executeGetAll();
    }

}