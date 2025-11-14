package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.note.GetNoteCardsQuery;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.NoteRepositoryPort;
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