package org.project.memospace.domain.port;

import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.browser.query.QuerySpec;

import java.util.List;

public interface CardQueryRepositoryPort {
    /**
     * Finds cards matching the query specification.
     */
    List<Card> findByQuery(QuerySpec querySpec, int limit);
}
