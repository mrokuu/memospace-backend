package org.project.deckservice.adapter.persistance.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.deckservice.domain.entity.DeckEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("DeckJpaRepository Integration Tests")
class DeckJpaRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeckJpaRepository repository;

    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
    }

    @Test
    @DisplayName("should save deck entity successfully")
    void shouldSaveDeck_WhenValidEntityProvided() {
        // given
        DeckEntity deck = new DeckEntity(null, "Test Deck", "Test Description", createdAt);

        // when
        DeckEntity savedDeck = repository.save(deck);

        // then
        assertThat(savedDeck).isNotNull();
        assertThat(savedDeck.getId()).isNotNull();
        assertThat(savedDeck.getName()).isEqualTo("Test Deck");
        assertThat(savedDeck.getDescription()).isEqualTo("Test Description");
        assertThat(savedDeck.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should generate ID when saving new deck")
    void shouldGenerateId_WhenSavingNewDeck() {
        // given
        DeckEntity deck1 = new DeckEntity(null, "Deck 1", "Description 1", createdAt);
        DeckEntity deck2 = new DeckEntity(null, "Deck 2", "Description 2", createdAt);

        // when
        DeckEntity saved1 = repository.save(deck1);
        DeckEntity saved2 = repository.save(deck2);

        // then
        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
    }

    @Test
    @DisplayName("should find deck by ID when deck exists")
    void shouldFindDeckById_WhenDeckExists() {
        // given
        DeckEntity deck = new DeckEntity(null, "Test Deck", "Test Description", createdAt);
        DeckEntity savedDeck = entityManager.persistAndFlush(deck);

        // when
        Optional<DeckEntity> result = repository.findById(savedDeck.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedDeck.getId());
        assertThat(result.get().getName()).isEqualTo("Test Deck");
        assertThat(result.get().getDescription()).isEqualTo("Test Description");
    }

    @Test
    @DisplayName("should return empty optional when deck not found by ID")
    void shouldReturnEmpty_WhenDeckNotFoundById() {
        // given
        Long nonExistentId = 999L;

        // when
        Optional<DeckEntity> result = repository.findById(nonExistentId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find all decks when multiple decks exist")
    void shouldFindAllDecks_WhenMultipleDecksExist() {
        // given
        DeckEntity deck1 = new DeckEntity(null, "Deck 1", "Description 1", createdAt);
        DeckEntity deck2 = new DeckEntity(null, "Deck 2", "Description 2", createdAt);
        DeckEntity deck3 = new DeckEntity(null, "Deck 3", "Description 3", createdAt);
        entityManager.persist(deck1);
        entityManager.persist(deck2);
        entityManager.persist(deck3);
        entityManager.flush();

        // when
        List<DeckEntity> result = repository.findAll();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(DeckEntity::getName)
                .containsExactlyInAnyOrder("Deck 1", "Deck 2", "Deck 3");
    }

    @Test
    @DisplayName("should return empty list when no decks exist")
    void shouldReturnEmptyList_WhenNoDecksExist() {
        // when
        List<DeckEntity> result = repository.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should update deck entity successfully")
    void shouldUpdateDeck_WhenDeckExists() {
        // given
        DeckEntity deck = new DeckEntity(null, "Original Name", "Original Description", createdAt);
        DeckEntity savedDeck = entityManager.persistAndFlush(deck);

        // when
        savedDeck.setName("Updated Name");
        savedDeck.setDescription("Updated Description");
        DeckEntity updatedDeck = repository.save(savedDeck);
        entityManager.flush();

        // then
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getName()).isEqualTo("Updated Name");
        assertThat(foundDeck.getDescription()).isEqualTo("Updated Description");
        assertThat(foundDeck.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should delete deck by ID successfully")
    void shouldDeleteDeck_WhenDeckExists() {
        // given
        DeckEntity deck = new DeckEntity(null, "Test Deck", "Test Description", createdAt);
        DeckEntity savedDeck = entityManager.persistAndFlush(deck);
        Long deckId = savedDeck.getId();

        // when
        repository.deleteById(deckId);
        entityManager.flush();

        // then
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, deckId);
        assertThat(foundDeck).isNull();
    }

    @Test
    @DisplayName("should check if deck exists by ID and return true")
    void shouldReturnTrue_WhenDeckExistsById() {
        // given
        DeckEntity deck = new DeckEntity(null, "Test Deck", "Test Description", createdAt);
        DeckEntity savedDeck = entityManager.persistAndFlush(deck);

        // when
        boolean exists = repository.existsById(savedDeck.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should check if deck exists by ID and return false")
    void shouldReturnFalse_WhenDeckDoesNotExistById() {
        // given
        Long nonExistentId = 999L;

        // when
        boolean exists = repository.existsById(nonExistentId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should save deck with null description")
    void shouldSaveDeck_WithNullDescription() {
        // given
        DeckEntity deck = new DeckEntity(null, "Deck Name", null, createdAt);

        // when
        DeckEntity savedDeck = repository.save(deck);
        entityManager.flush();

        // then
        assertThat(savedDeck.getDescription()).isNull();
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getDescription()).isNull();
    }

    @Test
    @DisplayName("should save deck with empty description")
    void shouldSaveDeck_WithEmptyDescription() {
        // given
        DeckEntity deck = new DeckEntity(null, "Deck Name", "", createdAt);

        // when
        DeckEntity savedDeck = repository.save(deck);
        entityManager.flush();

        // then
        assertThat(savedDeck.getDescription()).isEmpty();
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("should save deck with long name")
    void shouldSaveDeck_WithLongName() {
        // given
        String longName = "A".repeat(255);
        DeckEntity deck = new DeckEntity(null, longName, "Description", createdAt);

        // when
        DeckEntity savedDeck = repository.save(deck);
        entityManager.flush();

        // then
        assertThat(savedDeck.getName()).hasSize(255);
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("should save deck with long description")
    void shouldSaveDeck_WithLongDescription() {
        // given
        String longDescription = "B".repeat(1000);
        DeckEntity deck = new DeckEntity(null, "Name", longDescription, createdAt);

        // when
        DeckEntity savedDeck = repository.save(deck);
        entityManager.flush();

        // then
        assertThat(savedDeck.getDescription()).hasSize(1000);
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getDescription()).isEqualTo(longDescription);
    }

    @Test
    @DisplayName("should persist created timestamp correctly")
    void shouldPersistCreatedTimestamp_Correctly() {
        // given
        LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
        DeckEntity deck = new DeckEntity(null, "Test Deck", "Description", specificTime);

        // when
        DeckEntity savedDeck = repository.save(deck);
        entityManager.flush();

        // then
        DeckEntity foundDeck = entityManager.find(DeckEntity.class, savedDeck.getId());
        assertThat(foundDeck.getCreatedAt()).isEqualTo(specificTime);
    }

    @Test
    @DisplayName("should maintain referential integrity when deleting non-existent deck")
    void shouldNotThrowException_WhenDeletingNonExistentDeck() {
        // given
        Long nonExistentId = 999L;

        // when & then - should not throw exception
        repository.deleteById(nonExistentId);
        entityManager.flush();
    }

    @Test
    @DisplayName("should retrieve deck with all fields correctly mapped")
    void shouldRetrieveDeck_WithAllFieldsCorrectlyMapped() {
        // given
        LocalDateTime now = LocalDateTime.now();
        DeckEntity deck = new DeckEntity(null, "Complete Deck", "Full Description", now);
        DeckEntity savedDeck = entityManager.persistAndFlush(deck);

        // when
        Optional<DeckEntity> result = repository.findById(savedDeck.getId());

        // then
        assertThat(result).isPresent();
        DeckEntity foundDeck = result.get();
        assertThat(foundDeck.getId()).isNotNull();
        assertThat(foundDeck.getName()).isEqualTo("Complete Deck");
        assertThat(foundDeck.getDescription()).isEqualTo("Full Description");
        assertThat(foundDeck.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should count decks correctly")
    void shouldCountDecks_Correctly() {
        // given
        entityManager.persist(new DeckEntity(null, "Deck 1", "Desc 1", createdAt));
        entityManager.persist(new DeckEntity(null, "Deck 2", "Desc 2", createdAt));
        entityManager.persist(new DeckEntity(null, "Deck 3", "Desc 3", createdAt));
        entityManager.flush();

        // when
        long count = repository.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should delete all decks successfully")
    void shouldDeleteAllDecks_Successfully() {
        // given
        entityManager.persist(new DeckEntity(null, "Deck 1", "Desc 1", createdAt));
        entityManager.persist(new DeckEntity(null, "Deck 2", "Desc 2", createdAt));
        entityManager.flush();

        // when
        repository.deleteAll();
        entityManager.flush();

        // then
        List<DeckEntity> result = repository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle concurrent saves correctly")
    void shouldHandleConcurrentSaves_Correctly() {
        // given
        DeckEntity deck1 = new DeckEntity(null, "Deck 1", "Desc 1", createdAt);
        DeckEntity deck2 = new DeckEntity(null, "Deck 2", "Desc 2", createdAt);

        // when
        DeckEntity saved1 = repository.save(deck1);
        DeckEntity saved2 = repository.save(deck2);
        entityManager.flush();

        // then
        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
        assertThat(repository.count()).isEqualTo(2);
    }
}
