package org.memospace.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.notetype.GetAllNoteTypesQuery;
import org.memospace.model.NoteType;
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