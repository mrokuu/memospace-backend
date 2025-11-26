package com.example.cardservice.application.handler.card;

import com.example.cardservice.adapter.persistance.repository.CardRepositoryAdapter;
import com.example.cardservice.application.QueryHandler;
import com.example.cardservice.application.query.GetCardQuery;
import com.example.cardservice.domain.exception.CardNotFoundException;
import com.example.cardservice.domain.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCardQueryHandler implements QueryHandler<GetCardQuery, Card> {

    private final CardRepositoryAdapter cardRepositoryAdapter;

    @Override
    public Card handle(GetCardQuery query) {
//        return cardRepositoryAdapter.findById(query.cardId())
//                .orElseThrow(() -> new CardNotFoundException(query.cardId()));
        return null;
    }
}