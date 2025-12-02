package org.project.deckservice.adapter.persistance.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.deckservice.domain.entity.DeckEntity;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DeckJpaMapper Unit Tests")
class DeckJpaMapperTest {

    private DeckJpaMapper mapper;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        mapper = new DeckJpaMapper();
        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
    }

    @Test
    @DisplayName("should map deck domain to entity successfully")
    void shouldMapToEntity_WhenValidDeckProvided() {
        // given
        Deck deck = new Deck(1L, "Test Deck", "Test Description", createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deck");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should map entity to deck domain successfully")
    void shouldMapToDomain_WhenValidEntityProvided() {
        // given
        DeckEntity entity = new DeckEntity(1L, "Test Deck", "Test Description", createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deck");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should map deck with null ID to entity")
    void shouldMapToEntity_WhenDeckIdIsNull() {
        // given
        Deck deck = new Deck(null, "New Deck", "New Description", createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("New Deck");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should map deck with null description to entity")
    void shouldMapToEntity_WhenDescriptionIsNull() {
        // given
        Deck deck = new Deck(1L, "Deck Name", null, createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    @DisplayName("should map entity with null description to domain")
    void shouldMapToDomain_WhenDescriptionIsNull() {
        // given
        DeckEntity entity = new DeckEntity(1L, "Deck Name", null, createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    @DisplayName("should map deck with empty description to entity")
    void shouldMapToEntity_WhenDescriptionIsEmpty() {
        // given
        Deck deck = new Deck(1L, "Deck Name", "", createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("should map entity with empty description to domain")
    void shouldMapToDomain_WhenDescriptionIsEmpty() {
        // given
        DeckEntity entity = new DeckEntity(1L, "Deck Name", "", createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("should preserve all fields when mapping to entity")
    void shouldPreserveAllFields_WhenMappingToEntity() {
        // given
        LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
        Deck deck = new Deck(42L, "Specific Deck", "Specific Description", specificTime);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getName()).isEqualTo("Specific Deck");
        assertThat(result.getDescription()).isEqualTo("Specific Description");
        assertThat(result.getCreatedAt()).isEqualTo(specificTime);
    }

    @Test
    @DisplayName("should preserve all fields when mapping to domain")
    void shouldPreserveAllFields_WhenMappingToDomain() {
        // given
        LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
        DeckEntity entity = new DeckEntity(42L, "Specific Deck", "Specific Description", specificTime);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getName()).isEqualTo("Specific Deck");
        assertThat(result.getDescription()).isEqualTo("Specific Description");
        assertThat(result.getCreatedAt()).isEqualTo(specificTime);
    }

    @Test
    @DisplayName("should map deck with long name to entity")
    void shouldMapToEntity_WhenNameIsLong() {
        // given
        String longName = "A".repeat(255);
        Deck deck = new Deck(1L, longName, "Description", createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result.getName()).hasSize(255);
        assertThat(result.getName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("should map entity with long name to domain")
    void shouldMapToDomain_WhenNameIsLong() {
        // given
        String longName = "B".repeat(255);
        DeckEntity entity = new DeckEntity(1L, longName, "Description", createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result.getName()).hasSize(255);
        assertThat(result.getName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("should map deck with long description to entity")
    void shouldMapToEntity_WhenDescriptionIsLong() {
        // given
        String longDescription = "C".repeat(1000);
        Deck deck = new Deck(1L, "Name", longDescription, createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result.getDescription()).hasSize(1000);
        assertThat(result.getDescription()).isEqualTo(longDescription);
    }

    @Test
    @DisplayName("should map entity with long description to domain")
    void shouldMapToDomain_WhenDescriptionIsLong() {
        // given
        String longDescription = "D".repeat(1000);
        DeckEntity entity = new DeckEntity(1L, "Name", longDescription, createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result.getDescription()).hasSize(1000);
        assertThat(result.getDescription()).isEqualTo(longDescription);
    }

    @Test
    @DisplayName("should map deck with ID zero to entity")
    void shouldMapToEntity_WhenIdIsZero() {
        // given
        Deck deck = new Deck(0L, "Deck", "Description", createdAt);

        // when
        DeckEntity result = mapper.toEntity(deck);

        // then
        assertThat(result.getId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should map entity with ID zero to domain")
    void shouldMapToDomain_WhenIdIsZero() {
        // given
        DeckEntity entity = new DeckEntity(0L, "Deck", "Description", createdAt);

        // when
        Deck result = mapper.toDomain(entity);

        // then
        assertThat(result.getId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should be reversible - entity to domain to entity")
    void shouldBeReversible_EntityToDomainToEntity() {
        // given
        DeckEntity originalEntity = new DeckEntity(1L, "Original Deck", "Original Desc", createdAt);

        // when
        Deck domain = mapper.toDomain(originalEntity);
        DeckEntity resultEntity = mapper.toEntity(domain);

        // then
        assertThat(resultEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(resultEntity.getName()).isEqualTo(originalEntity.getName());
        assertThat(resultEntity.getDescription()).isEqualTo(originalEntity.getDescription());
        assertThat(resultEntity.getCreatedAt()).isEqualTo(originalEntity.getCreatedAt());
    }

    @Test
    @DisplayName("should be reversible - domain to entity to domain")
    void shouldBeReversible_DomainToEntityToDomain() {
        // given
        Deck originalDeck = new Deck(1L, "Original Deck", "Original Desc", createdAt);

        // when
        DeckEntity entity = mapper.toEntity(originalDeck);
        Deck resultDeck = mapper.toDomain(entity);

        // then
        assertThat(resultDeck.getId()).isEqualTo(originalDeck.getId());
        assertThat(resultDeck.getName()).isEqualTo(originalDeck.getName());
        assertThat(resultDeck.getDescription()).isEqualTo(originalDeck.getDescription());
        assertThat(resultDeck.getCreatedAt()).isEqualTo(originalDeck.getCreatedAt());
    }

    @Test
    @DisplayName("should handle various timestamps when mapping to entity")
    void shouldHandleVariousTimestamps_WhenMappingToEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime future = LocalDateTime.of(2030, 12, 31, 23, 59);

        Deck deck1 = new Deck(1L, "Deck 1", "Desc", now);
        Deck deck2 = new Deck(2L, "Deck 2", "Desc", past);
        Deck deck3 = new Deck(3L, "Deck 3", "Desc", future);

        // when
        DeckEntity entity1 = mapper.toEntity(deck1);
        DeckEntity entity2 = mapper.toEntity(deck2);
        DeckEntity entity3 = mapper.toEntity(deck3);

        // then
        assertThat(entity1.getCreatedAt()).isEqualTo(now);
        assertThat(entity2.getCreatedAt()).isEqualTo(past);
        assertThat(entity3.getCreatedAt()).isEqualTo(future);
    }

    @Test
    @DisplayName("should handle various timestamps when mapping to domain")
    void shouldHandleVariousTimestamps_WhenMappingToDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime future = LocalDateTime.of(2030, 12, 31, 23, 59);

        DeckEntity entity1 = new DeckEntity(1L, "Deck 1", "Desc", now);
        DeckEntity entity2 = new DeckEntity(2L, "Deck 2", "Desc", past);
        DeckEntity entity3 = new DeckEntity(3L, "Deck 3", "Desc", future);

        // when
        Deck deck1 = mapper.toDomain(entity1);
        Deck deck2 = mapper.toDomain(entity2);
        Deck deck3 = mapper.toDomain(entity3);

        // then
        assertThat(deck1.getCreatedAt()).isEqualTo(now);
        assertThat(deck2.getCreatedAt()).isEqualTo(past);
        assertThat(deck3.getCreatedAt()).isEqualTo(future);
    }
}
