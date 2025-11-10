package org.project.memospace.application.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.note.GetNoteCardsQuery;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNoteCardsQueryHandler implements QueryHandler<GetNoteCardsQuery, List<Card>> {

    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;

    @Override
    public List<Card> handle(GetNoteCardsQuery query) {

        if (!noteRepository.existsById(query.getNoteId())) {
            throw new NoteNotFoundException(query.getNoteId());
        }

        return cardRepository.findAllByNoteId(query.getNoteId());
    }
}