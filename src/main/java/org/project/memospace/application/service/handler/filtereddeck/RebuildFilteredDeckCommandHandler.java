package org.project.memospace.application.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.RebuildFilteredDeckResult;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.filtereddeck.RebuildFilteredDeckCommand;
import org.project.memospace.domain.exception.FilteredDeckNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.FilteredDeck;
import org.project.memospace.domain.model.FilteredDeckMembership;
import org.project.memospace.domain.model.browser.query.QuerySpec;
import org.project.memospace.domain.port.CardQueryRepositoryPort;
import org.project.memospace.domain.port.FilteredDeckMembershipRepositoryPort;
import org.project.memospace.domain.port.FilteredDeckRepositoryPort;
import org.project.memospace.domain.service.FilteredDeckService;
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
