package org.memospace.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.memospace.dto.RebuildFilteredDeckResult;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.filtereddeck.RebuildFilteredDeckCommand;
import org.memospace.exception.FilteredDeckNotFoundException;
import org.memospace.model.Card;
import org.memospace.model.FilteredDeck;
import org.memospace.model.FilteredDeckMembership;
import org.memospace.model.browser.query.QuerySpec;
import org.memospace.port.CardQueryRepositoryPort;
import org.memospace.port.FilteredDeckMembershipRepositoryPort;
import org.memospace.port.FilteredDeckRepositoryPort;
import org.memospace.service.FilteredDeckService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class RebuildFilteredDeckCommandHandler implements CommandHandler<RebuildFilteredDeckCommand, RebuildFilteredDeckResult> {

    private final FilteredDeckRepositoryPort filteredDeckRepository;
    private final FilteredDeckMembershipRepositoryPort membershipRepository;
    private final CardQueryRepositoryPort cardQueryRepository;
    private final FilteredDeckService filteredDeckService;
    private final Clock clock;

    @Override
    public RebuildFilteredDeckResult handle(RebuildFilteredDeckCommand command) {
        Instant now = clock.instant();

        // Find existing deck
        FilteredDeck filteredDeck = filteredDeckRepository.findById(command.filteredDeckId())
                .orElseThrow(() -> new FilteredDeckNotFoundException(command.filteredDeckId()));

        // Delete old memberships
        membershipRepository.deleteAllFor(filteredDeck.id());

        // Fetch candidate cards using original query
        List<Card> candidates = cardQueryRepository.findByQuery(
                new QuerySpec(filteredDeck.query()),
                filteredDeck.limit() * 2
        );

        // Build new membership
        FilteredDeckService.BuildPlan plan = filteredDeckService.buildFromQuery(
                filteredDeck.query(),
                filteredDeck.limit(),
                filteredDeck.mode(),
                candidates,
                now
        );

        // Create and save new memberships
        List<FilteredDeckMembership> memberships = filteredDeckService.createMemberships(
                plan,
                filteredDeck.id(),
                now
        );
        membershipRepository.saveAll(memberships);

        // Update last built timestamp
        FilteredDeck updatedDeck = filteredDeck.markRebuilt(now);
        FilteredDeck savedDeck = filteredDeckRepository.save(updatedDeck);

        return new RebuildFilteredDeckResult(savedDeck, plan.getTotal());
    }
}
