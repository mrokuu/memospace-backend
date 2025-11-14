package org.memospace.port;

import org.memospace.model.Card;
import org.memospace.model.browser.query.QuerySpec;

import java.util.List;

public interface CardQueryRepositoryPort {
    /**
     * Finds cards matching the query specification.
     */
    List<Card> findByQuery(QuerySpec querySpec, int limit);
}
