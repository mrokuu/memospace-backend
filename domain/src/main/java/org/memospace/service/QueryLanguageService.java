package org.memospace.service;

import org.memospace.exception.InvalidQueryException;
import org.memospace.model.browser.query.QuerySpec;

public class QueryLanguageService {

    public QuerySpec parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidQueryException("Query cannot be null or empty", 0);
        }

        // For now, just wrap the query as-is
        // Full implementation would parse deck:, tag:, is:, prop:, etc.
        return new QuerySpec(query);
    }
}
