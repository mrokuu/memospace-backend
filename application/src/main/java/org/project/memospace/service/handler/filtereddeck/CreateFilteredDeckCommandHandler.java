package org.project.memospace.application.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.dto.CreateFilteredDeckResult;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.filtereddeck.CreateFilteredDeckCommand;
import org.project.memospace.domain.exception.InvalidQueryException;
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
public class CreateFilteredDeckCommandHandler implements CommandHandler<CreateFilteredDeckCommand, CreateFilteredDeckResult> {

    private final FilteredDeckRepositoryPort filteredDeckRepository;
    private final FilteredDeckMembershipRepositoryPort membershipRepository;
    private final CardQueryRepositoryPort cardQueryRepository;
    private final FilteredDeckService filteredDeckService;
    private final Clock clock;

    @Override
    public CreateFilteredDeckResult handle(CreateFilteredDeckCommand command) {
        Instant now = clock.instant();

        // Validate inputs
        validateCommand(command);

        // Create filtered deck domain object
        FilteredDeck filteredDeck = FilteredDeck.create(
                command.name(),
                command.ownerDeckId(),
                command.query(),
                command.limit(),
                command.mode(),
                command.returnPolicy(),
                command.expiresAt(),
                now
        );

        // Fetch candidate cards using query
        List<Card> candidates = cardQueryRepository.findByQuery(
                new QuerySpec(command.query()),
                command.limit() * 2  // Fetch extra to allow filtering
        );

        // Build filtered deck using service
        FilteredDeckService.BuildPlan plan = filteredDeckService.buildFromQuery(
                command.query(),
                command.limit(),
                command.mode(),
                candidates,
                now
        );

        // Save filtered deck
        FilteredDeck savedDeck = filteredDeckRepository.save(filteredDeck);

        // Create and save memberships
        List<FilteredDeckMembership> memberships = filteredDeckService.createMemberships(
                plan,
                savedDeck.id(),
                now
        );
        membershipRepository.saveAll(memberships);

        return new CreateFilteredDeckResult(savedDeck, plan.getTotal());
    }

    private void validateCommand(CreateFilteredDeckCommand command) {
        if (command.query() == null || command.query().trim().isEmpty()) {
            throw new InvalidQueryException("Query cannot be null or empty", 0);
        }

        if (command.limit() < 1 || command.limit() > 10000) {
            throw new IllegalArgumentException("Limit must be between 1 and 10000");
        }

        if (command.expiresAt() != null && command.expiresAt().isBefore(clock.instant())) {
            throw new IllegalArgumentException("ExpiresAt must be in the future");
        }
    }
}
