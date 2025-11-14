package org.memospace.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.filtereddeck.GetNextForFilteredDeckQuery;
import org.memospace.exception.FilteredDeckNotFoundException;
import org.memospace.model.Card;
import org.memospace.model.FilteredDeck;
import org.memospace.port.FilteredDeckMembershipRepositoryPort;
import org.memospace.port.FilteredDeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNextForFilteredDeckQueryHandler implements QueryHandler<GetNextForFilteredDeckQuery, List<Card>> {

    private final FilteredDeckRepositoryPort filteredDeckRepository;
    private final FilteredDeckMembershipRepositoryPort membershipRepository;
    private final Clock clock;

    @Override
    public List<Card> handle(GetNextForFilteredDeckQuery query) {
        Instant now = clock.instant();

        // Verify deck exists
        FilteredDeck filteredDeck = filteredDeckRepository.findById(query.getFilteredDeckId())
                .orElseThrow(() -> new FilteredDeckNotFoundException(query.getFilteredDeckId()));

        // Check if expired
        if (filteredDeck.isExpired(now)) {
            return List.of();  // Return empty list if expired
        }

        // Fetch next due cards from membership
        return membershipRepository.findNextDue(query.getFilteredDeckId(), now, query.getLimit());
    }
}
