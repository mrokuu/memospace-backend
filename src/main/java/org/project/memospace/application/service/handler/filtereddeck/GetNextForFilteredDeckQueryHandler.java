package org.project.memospace.application.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.filtereddeck.GetNextForFilteredDeckQuery;
import org.project.memospace.domain.exception.FilteredDeckNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.FilteredDeck;
import org.project.memospace.domain.port.FilteredDeckMembershipRepositoryPort;
import org.project.memospace.domain.port.FilteredDeckRepositoryPort;
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
