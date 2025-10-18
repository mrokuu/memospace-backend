package org.project.memospace.domain.service;

import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.FilteredDeck;
import org.project.memospace.domain.model.FilteredDeckMembership;
import org.project.memospace.domain.model.FilteredMode;
import org.project.memospace.domain.model.ReturnPolicy;
import org.project.memospace.domain.model.browser.query.QuerySpec;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FilteredDeckService {
    private final QueryLanguageService queryLanguageService;

    public FilteredDeckService(QueryLanguageService queryLanguageService) {
        this.queryLanguageService = queryLanguageService;
    }

    /**
     * Builds a plan for filtered deck membership from candidate cards.
     */
    public BuildPlan buildFromQuery(String query, int limit, FilteredMode mode, List<Card> candidates, Instant now) {
        QuerySpec querySpec = queryLanguageService.parse(query);

        List<Card> filteredCards = filterByMode(candidates, mode, now);
        List<Card> limited = limitCards(filteredCards, limit);

        return new BuildPlan(querySpec, limited);
    }

    /**
     * Checks if a filtered deck should automatically return cards based on policy.
     */
    public boolean shouldAutoReturn(Instant now, Instant cutoff, FilteredDeck filteredDeck) {
        ReturnPolicy policy = filteredDeck.returnPolicy();

        switch (policy) {
            case WHEN_EMPTY:
                // Cannot determine emptiness here; application layer handles this
                return false;
            case ON_DELETE:
                // Never auto-return; only on explicit delete
                return false;
            case AT_CUTOFF:
                // Return at cutoff if past creation time
                return now.isAfter(cutoff) && cutoff.isAfter(filteredDeck.createdAt());
            default:
                return false;
        }
    }

    /**
     * Creates membership records from a build plan.
     */
    public List<FilteredDeckMembership> createMemberships(BuildPlan plan, java.util.UUID filteredDeckId, Instant now) {
        List<FilteredDeckMembership> memberships = new ArrayList<>();
        List<Card> cards = plan.getCards();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            memberships.add(FilteredDeckMembership.create(filteredDeckId, card.id(), i, now));
        }

        return memberships;
    }

    private List<Card> filterByMode(List<Card> candidates, FilteredMode mode, Instant now) {
        if (mode == FilteredMode.REVIEW_ONLY) {
            // Only include cards that have been reviewed before (not new cards)
            // New cards have repetitions = 0
            return candidates.stream()
                    .filter(card -> card.repetitions() > 0)
                    .toList();
        }

        // INCLUDE_NEW: include all candidates
        return candidates;
    }

    private List<Card> limitCards(List<Card> cards, int limit) {
        if (cards.size() <= limit) {
            return cards;
        }
        return cards.subList(0, limit);
    }

    /**
     * Result of building a filtered deck.
     */
    public static class BuildPlan {
        private final QuerySpec querySpec;
        private final List<Card> cards;

        public BuildPlan(QuerySpec querySpec, List<Card> cards) {
            this.querySpec = querySpec;
            this.cards = cards;
        }

        public QuerySpec getQuerySpec() {
            return querySpec;
        }

        public List<Card> getCards() {
            return cards;
        }

        public int getTotal() {
            return cards.size();
        }
    }
}
