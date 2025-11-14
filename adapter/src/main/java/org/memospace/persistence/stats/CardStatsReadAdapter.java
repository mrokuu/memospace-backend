package org.memospace.persistence.stats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.memospace.model.stats.DeckScope;
import org.memospace.port.stats.CardStatsReadPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter for reading card data for statistics.
 */
@Component
@RequiredArgsConstructor
public class CardStatsReadAdapter implements CardStatsReadPort {

    private final EntityManager entityManager;

    @Override
    public List<LocalDateTime> getDueDatesForForecast(DeckScope scope) {
        String queryStr;
        TypedQuery<LocalDateTime> query;

        if (scope.isAll()) {
            queryStr = "SELECT c.dueAt FROM CardEntity c ORDER BY c.dueAt";
            query = entityManager.createQuery(queryStr, LocalDateTime.class);
        } else {
            queryStr = "SELECT c.dueAt FROM CardEntity c WHERE c.deckId = :deckId ORDER BY c.dueAt";
            query = entityManager.createQuery(queryStr, LocalDateTime.class);
            query.setParameter("deckId", scope.deckId());
        }

        return query.getResultList();
    }
}
