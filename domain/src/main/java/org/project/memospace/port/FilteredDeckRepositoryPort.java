package org.project.memospace.domain.port;

import org.project.memospace.domain.model.FilteredDeck;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FilteredDeckRepositoryPort {
    FilteredDeck save(FilteredDeck filteredDeck);

    Optional<FilteredDeck> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    List<FilteredDeck> findExpired(Instant now);
}
