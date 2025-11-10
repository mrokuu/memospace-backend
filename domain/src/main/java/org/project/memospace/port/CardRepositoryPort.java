package org.project.memospace.domain.port;

import org.project.memospace.domain.model.Card;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepositoryPort {
    Card save(Card card);

    Optional<Card> findById(Long id);

    List<Card> findDueCards(Long deckId, int limit);

    List<Card> findDueCards(int limit);

    List<Card> searchCards(Long deckId, String searchTerm, Boolean onlyDue, int page, int size);

    void deleteById(Long id);

    void deleteByDeckId(Long deckId);

    boolean existsById(Long id);

    List<Card> saveAll(List<Card> cards);

    void deleteAllByNoteId(UUID noteId);

    List<Card> findAllByNoteId(UUID noteId);
}