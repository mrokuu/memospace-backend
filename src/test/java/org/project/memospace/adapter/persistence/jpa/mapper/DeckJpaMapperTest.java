package org.project.memospace.adapter.persistence.jpa.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.persistence.jpa.entity.DeckEntity;
import org.project.memospace.domain.model.Deck;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeckJpaMapperTest {

    private DeckJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DeckJpaMapper();
    }

    @Test
    void shouldMapDomainToEntity() {
        // Given
        Deck domain = Deck.builder()
                .id(1L)
                .name("Spanish")
                .description("Spanish vocabulary deck")
                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        // When
        DeckEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(domain.id(), entity.getId());
        assertEquals(domain.name(), entity.getName());
        assertEquals(domain.description(), entity.getDescription());
        assertEquals(domain.createdAt(), entity.getCreatedAt());
    }

    @Test
    void shouldMapEntityToDomain() {
        // Given
        DeckEntity entity = DeckEntity.builder()
                .id(2L)
                .name("French")
                .description("French vocabulary deck")
                .createdAt(LocalDateTime.of(2025, 1, 2, 14, 30))
                .build();

        // When
        Deck domain = mapper.toDomain(entity);

        // Then
        assertEquals(entity.getId(), domain.id());
        assertEquals(entity.getName(), domain.name());
        assertEquals(entity.getDescription(), domain.description());
        assertEquals(entity.getCreatedAt(), domain.createdAt());
    }

    @Test
    void shouldMapDeckWithNullDescription() {
        // Given
        Deck domain = Deck.builder()
                .id(3L)
                .name("Math")
                .description(null)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DeckEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(domain.id(), entity.getId());
        assertEquals(domain.name(), entity.getName());
        assertNull(entity.getDescription());
        assertEquals(domain.createdAt(), entity.getCreatedAt());
    }

    @Test
    void shouldMapDeckWithNullId() {
        // Given
        Deck domain = Deck.create("New Deck", "A new deck");

        // When
        DeckEntity entity = mapper.toEntity(domain);

        // Then
        assertNull(entity.getId());
        assertEquals(domain.name(), entity.getName());
        assertEquals(domain.description(), entity.getDescription());
    }

    @Test
    void shouldRoundTripDomainToEntityToDomain() {
        // Given
        Deck original = Deck.builder()
                .id(5L)
                .name("Chemistry")
                .description("Chemistry formulas and concepts")
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();

        // When
        DeckEntity entity = mapper.toEntity(original);
        Deck roundTripped = mapper.toDomain(entity);

        // Then
        assertEquals(original.id(), roundTripped.id());
        assertEquals(original.name(), roundTripped.name());
        assertEquals(original.description(), roundTripped.description());
        assertEquals(original.createdAt(), roundTripped.createdAt());
    }

    @Test
    void shouldRoundTripEntityToDomainToEntity() {
        // Given
        DeckEntity original = DeckEntity.builder()
                .id(6L)
                .name("Physics")
                .description("Physics laws and equations")
                .createdAt(LocalDateTime.of(2025, 1, 20, 9, 30))
                .build();

        // When
        Deck domain = mapper.toDomain(original);
        DeckEntity roundTripped = mapper.toEntity(domain);

        // Then
        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getName(), roundTripped.getName());
        assertEquals(original.getDescription(), roundTripped.getDescription());
        assertEquals(original.getCreatedAt(), roundTripped.getCreatedAt());
    }

    @Test
    void shouldMapMultipleDecksIndependently() {
        // Given
        Deck deck1 = Deck.builder()
                .id(1L)
                .name("Deck 1")
                .description("Description 1")
                .createdAt(LocalDateTime.now())
                .build();

        Deck deck2 = Deck.builder()
                .id(2L)
                .name("Deck 2")
                .description("Description 2")
                .createdAt(LocalDateTime.now().plusHours(1))
                .build();

        // When
        DeckEntity entity1 = mapper.toEntity(deck1);
        DeckEntity entity2 = mapper.toEntity(deck2);

        // Then
        assertNotEquals(entity1.getId(), entity2.getId());
        assertNotEquals(entity1.getName(), entity2.getName());
        assertNotEquals(entity1.getCreatedAt(), entity2.getCreatedAt());
    }

    @Test
    void shouldPreserveEmptyDescription() {
        // Given
        Deck domain = Deck.builder()
                .id(7L)
                .name("Test")
                .description("")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DeckEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals("", entity.getDescription());
    }

    @Test
    void shouldPreserveLongDescription() {
        // Given
        String longDescription = "A".repeat(10000);
        Deck domain = Deck.builder()
                .id(8L)
                .name("Test")
                .description(longDescription)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DeckEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(10000, entity.getDescription().length());
    }
}
