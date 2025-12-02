package org.project.deckservice.adapter.persistance.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.jpa.DeckJpaRepository;
import org.project.deckservice.adapter.persistance.mapper.DeckJpaMapper;
import org.project.deckservice.domain.entity.DeckEntity;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeckRepositoryAdapter Unit Tests")
class DeckRepositoryAdapterTest {

    @Mock
    private DeckJpaRepository jpaRepository;

    @Mock
    private DeckJpaMapper mapper;

    @InjectMocks
    private DeckRepositoryAdapter repositoryAdapter;

    private Deck testDeck;
    private DeckEntity testEntity;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        testDeck = new Deck(1L, "Test Deck", "Test Description", createdAt);
        testEntity = new DeckEntity(1L, "Test Deck", "Test Description", createdAt);
    }

    @Test
    @DisplayName("should save deck successfully")
    void shouldSaveDeck_WhenValidDeckProvided() {
        // given
        when(mapper.toEntity(testDeck)).thenReturn(testEntity);
        when(jpaRepository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDomain(testEntity)).thenReturn(testDeck);

        // when
        Deck result = repositoryAdapter.save(testDeck);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testDeck);
        verify(mapper).toEntity(testDeck);
        verify(jpaRepository).save(testEntity);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("should convert deck to entity before saving")
    void shouldConvertToEntity_BeforeSaving() {
        // given
        when(mapper.toEntity(testDeck)).thenReturn(testEntity);
        when(jpaRepository.save(any(DeckEntity.class))).thenReturn(testEntity);
        when(mapper.toDomain(testEntity)).thenReturn(testDeck);

        // when
        repositoryAdapter.save(testDeck);

        // then
        var inOrder = inOrder(mapper, jpaRepository);
        inOrder.verify(mapper).toEntity(testDeck);
        inOrder.verify(jpaRepository).save(testEntity);
        inOrder.verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("should find deck by ID when exists")
    void shouldFindDeckById_WhenDeckExists() {
        // given
        Long deckId = 1L;
        when(jpaRepository.findById(deckId)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testDeck);

        // when
        Optional<Deck> result = repositoryAdapter.findById(deckId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testDeck);
        verify(jpaRepository).findById(deckId);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("should return empty optional when deck not found by ID")
    void shouldReturnEmpty_WhenDeckNotFoundById() {
        // given
        Long deckId = 999L;
        when(jpaRepository.findById(deckId)).thenReturn(Optional.empty());

        // when
        Optional<Deck> result = repositoryAdapter.findById(deckId);

        // then
        assertThat(result).isEmpty();
        verify(jpaRepository).findById(deckId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("should find all decks when multiple decks exist")
    void shouldFindAllDecks_WhenMultipleDecksExist() {
        // given
        DeckEntity entity1 = new DeckEntity(1L, "Deck 1", "Desc 1", createdAt);
        DeckEntity entity2 = new DeckEntity(2L, "Deck 2", "Desc 2", createdAt);
        Deck deck1 = new Deck(1L, "Deck 1", "Desc 1", createdAt);
        Deck deck2 = new Deck(2L, "Deck 2", "Desc 2", createdAt);

        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(deck1);
        when(mapper.toDomain(entity2)).thenReturn(deck2);

        // when
        List<Deck> result = repositoryAdapter.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(deck1, deck2);
        verify(jpaRepository).findAll();
        verify(mapper, times(2)).toDomain(any(DeckEntity.class));
    }

    @Test
    @DisplayName("should return empty list when no decks exist")
    void shouldReturnEmptyList_WhenNoDecksExist() {
        // given
        when(jpaRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Deck> result = repositoryAdapter.findAll();

        // then
        assertThat(result).isEmpty();
        verify(jpaRepository).findAll();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("should delete deck by ID")
    void shouldDeleteDeckById_WhenIdProvided() {
        // given
        Long deckId = 1L;
        doNothing().when(jpaRepository).deleteById(deckId);

        // when
        repositoryAdapter.deleteById(deckId);

        // then
        verify(jpaRepository).deleteById(deckId);
    }

    @Test
    @DisplayName("should check if deck exists by ID and return true")
    void shouldReturnTrue_WhenDeckExistsById() {
        // given
        Long deckId = 1L;
        when(jpaRepository.existsById(deckId)).thenReturn(true);

        // when
        boolean result = repositoryAdapter.existsById(deckId);

        // then
        assertThat(result).isTrue();
        verify(jpaRepository).existsById(deckId);
    }

    @Test
    @DisplayName("should check if deck exists by ID and return false")
    void shouldReturnFalse_WhenDeckDoesNotExistById() {
        // given
        Long deckId = 999L;
        when(jpaRepository.existsById(deckId)).thenReturn(false);

        // when
        boolean result = repositoryAdapter.existsById(deckId);

        // then
        assertThat(result).isFalse();
        verify(jpaRepository).existsById(deckId);
    }

    @Test
    @DisplayName("should map each entity to domain when finding all")
    void shouldMapEachEntity_WhenFindingAll() {
        // given
        DeckEntity entity1 = new DeckEntity(1L, "Deck 1", "Desc 1", createdAt);
        DeckEntity entity2 = new DeckEntity(2L, "Deck 2", "Desc 2", createdAt);
        DeckEntity entity3 = new DeckEntity(3L, "Deck 3", "Desc 3", createdAt);
        Deck deck1 = new Deck(1L, "Deck 1", "Desc 1", createdAt);
        Deck deck2 = new Deck(2L, "Deck 2", "Desc 2", createdAt);
        Deck deck3 = new Deck(3L, "Deck 3", "Desc 3", createdAt);

        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2, entity3));
        when(mapper.toDomain(entity1)).thenReturn(deck1);
        when(mapper.toDomain(entity2)).thenReturn(deck2);
        when(mapper.toDomain(entity3)).thenReturn(deck3);

        // when
        List<Deck> result = repositoryAdapter.findAll();

        // then
        assertThat(result).hasSize(3);
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
        verify(mapper).toDomain(entity3);
    }

    @Test
    @DisplayName("should save deck with null description")
    void shouldSaveDeck_WithNullDescription() {
        // given
        Deck deckWithNullDesc = new Deck(null, "Deck Name", null, createdAt);
        DeckEntity entityWithNullDesc = new DeckEntity(null, "Deck Name", null, createdAt);
        DeckEntity savedEntity = new DeckEntity(1L, "Deck Name", null, createdAt);
        Deck savedDeck = new Deck(1L, "Deck Name", null, createdAt);

        when(mapper.toEntity(deckWithNullDesc)).thenReturn(entityWithNullDesc);
        when(jpaRepository.save(entityWithNullDesc)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDeck);

        // when
        Deck result = repositoryAdapter.save(deckWithNullDesc);

        // then
        assertThat(result.getDescription()).isNull();
        verify(jpaRepository).save(entityWithNullDesc);
    }

    @Test
    @DisplayName("should handle deck with ID zero")
    void shouldHandleDeck_WithIdZero() {
        // given
        Long zeroId = 0L;
        when(jpaRepository.existsById(zeroId)).thenReturn(true);

        // when
        boolean result = repositoryAdapter.existsById(zeroId);

        // then
        assertThat(result).isTrue();
        verify(jpaRepository).existsById(zeroId);
    }

    @Test
    @DisplayName("should delete deck by specific ID")
    void shouldDeleteDeck_BySpecificId() {
        // given
        Long deckId = 42L;
        doNothing().when(jpaRepository).deleteById(deckId);

        // when
        repositoryAdapter.deleteById(deckId);

        // then
        verify(jpaRepository).deleteById(eq(42L));
    }

    @Test
    @DisplayName("should preserve order when finding all decks")
    void shouldPreserveOrder_WhenFindingAllDecks() {
        // given
        DeckEntity entity1 = new DeckEntity(3L, "Third", "Desc", createdAt);
        DeckEntity entity2 = new DeckEntity(1L, "First", "Desc", createdAt);
        DeckEntity entity3 = new DeckEntity(2L, "Second", "Desc", createdAt);
        Deck deck1 = new Deck(3L, "Third", "Desc", createdAt);
        Deck deck2 = new Deck(1L, "First", "Desc", createdAt);
        Deck deck3 = new Deck(2L, "Second", "Desc", createdAt);

        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2, entity3));
        when(mapper.toDomain(entity1)).thenReturn(deck1);
        when(mapper.toDomain(entity2)).thenReturn(deck2);
        when(mapper.toDomain(entity3)).thenReturn(deck3);

        // when
        List<Deck> result = repositoryAdapter.findAll();

        // then
        assertThat(result).containsExactly(deck1, deck2, deck3);
    }
}
