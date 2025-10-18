package org.project.memospace.domain.port;

import org.project.memospace.domain.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepositoryPort {
    Deck save(Deck deck);

    Optional<Deck> findById(Long id);

    List<Deck> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}