package com.example.cardservice.adapter.persistance.repository;

import com.example.cardservice.adapter.persistance.jpa.CardJpaRepository;
import com.example.cardservice.adapter.persistance.mapper.CardJpaMapper;
import com.example.cardservice.domain.entity.CardEntity;
import com.example.cardservice.domain.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CardRepositoryAdapter {

    private final CardJpaRepository jpaRepository;
    private final CardJpaMapper mapper;

    public Card save(Card card) {
        CardEntity entity = mapper.toEntity(card);
        CardEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
