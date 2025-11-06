package org.project.memospace.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.persistence.jpa.mapper.CardJpaMapper;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.browser.query.QuerySpec;
import org.project.memospace.domain.port.CardQueryRepositoryPort;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;

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
