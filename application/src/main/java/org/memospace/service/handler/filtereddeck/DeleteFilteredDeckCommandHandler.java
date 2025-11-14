package org.memospace.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.filtereddeck.DeleteFilteredDeckCommand;
import org.memospace.exception.FilteredDeckNotFoundException;
import org.memospace.port.FilteredDeckMembershipRepositoryPort;
import org.memospace.port.FilteredDeckRepositoryPort;
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
