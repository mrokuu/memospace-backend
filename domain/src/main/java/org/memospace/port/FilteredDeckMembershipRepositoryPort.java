package org.memospace.port;

import org.memospace.model.Card;
import org.memospace.model.FilteredDeckMembership;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface FilteredDeckMembershipRepositoryPort {
    void saveAll(List<FilteredDeckMembership> memberships);

    void deleteAllFor(UUID filteredDeckId);

    void removeCard(UUID filteredDeckId, Long cardId);

    List<Card> findNextDue(UUID filteredDeckId, Instant now, int limit);

    int countFor(UUID filteredDeckId);
}
