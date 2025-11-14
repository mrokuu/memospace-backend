package org.memospace.persistence;

import lombok.RequiredArgsConstructor;
import org.memospace.persistence.jpa.mapper.CardJpaMapper;
import org.memospace.model.Card;
import org.memospace.model.browser.query.QuerySpec;
import org.memospace.port.CardQueryRepositoryPort;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.memospace.persistence.jpa.entity.CardEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CardQueryRepositoryAdapter implements CardQueryRepositoryPort {
    private final EntityManager entityManager;
    private final CardJpaMapper cardMapper;

    @Override
    public List<Card> findByQuery(QuerySpec querySpec, int limit) {
        String jpql = "SELECT c FROM CardEntity c ORDER BY c.dueAt ASC";

        TypedQuery<CardEntity> query = entityManager.createQuery(jpql, CardEntity.class);
        query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(cardMapper::toDomain)
                .toList();
    }
}
