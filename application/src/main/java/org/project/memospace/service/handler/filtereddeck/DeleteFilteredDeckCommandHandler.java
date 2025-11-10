package org.project.memospace.application.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.filtereddeck.DeleteFilteredDeckCommand;
import org.project.memospace.domain.exception.FilteredDeckNotFoundException;
import org.project.memospace.domain.port.FilteredDeckMembershipRepositoryPort;
import org.project.memospace.domain.port.FilteredDeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteFilteredDeckCommandHandler implements CommandHandler<DeleteFilteredDeckCommand, Void> {

    private final FilteredDeckRepositoryPort filteredDeckRepository;
    private final FilteredDeckMembershipRepositoryPort membershipRepository;

    @Override
    public Void handle(DeleteFilteredDeckCommand command) {
        // Verify deck exists
        if (!filteredDeckRepository.existsById(command.filteredDeckId())) {
            throw new FilteredDeckNotFoundException(command.filteredDeckId());
        }

        // Delete memberships first (returns cards)
        membershipRepository.deleteAllFor(command.filteredDeckId());

        // Delete filtered deck
        filteredDeckRepository.deleteById(command.filteredDeckId());

        return null;
    }
}
