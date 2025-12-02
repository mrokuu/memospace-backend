package org.project.deckservice.application.handler.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetDeckQueryHandler Unit Tests")
class GetDeckQueryHandlerTest {

    @Mock
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @InjectMocks
    private GetDeckQueryHandler handler;

    private GetDeckQuery query;
    private Deck testDeck;

    @BeforeEach
    void setUp() {
        query = new GetDeckQuery(1L);
        testDeck = new Deck(1L, "Test Deck", "Test Description", LocalDateTime.now());
    }

    @Test
    @DisplayName("should return deck when deck exists")
    void shouldReturnDeck_WhenDeckExists() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(testDeck));

        // when
        Deck result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testDeck);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deck");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        verify(deckRepositoryAdapter).findById(1L);
    }

    @Test
    @DisplayName("should throw DeckNotFoundException when deck does not exist")
    void shouldThrowException_WhenDeckNotFound() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> handler.handle(query))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 1");

        verify(deckRepositoryAdapter).findById(1L);
    }

    @Test
    @DisplayName("should return deck with null description")
    void shouldReturnDeck_WhenDescriptionIsNull() {
        // given
        Deck deckWithNullDesc = new Deck(1L, "Deck Name", null, LocalDateTime.now());
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(deckWithNullDesc));

        // when
        Deck result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
        verify(deckRepositoryAdapter).findById(1L);
    }

    @Test
    @DisplayName("should return deck with empty description")
    void shouldReturnDeck_WhenDescriptionIsEmpty() {
        // given
        Deck deckWithEmptyDesc = new Deck(1L, "Deck Name", "", LocalDateTime.now());
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(deckWithEmptyDesc));

        // when
        Deck result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEmpty();
        verify(deckRepositoryAdapter).findById(1L);
    }

    @Test
    @DisplayName("should call repository with correct deck ID")
    void shouldCallRepository_WithCorrectDeckId() {
        // given
        Long deckId = 42L;
        GetDeckQuery queryWithSpecificId = new GetDeckQuery(deckId);
        Deck deck = new Deck(deckId, "Name", "Desc", LocalDateTime.now());
        when(deckRepositoryAdapter.findById(deckId)).thenReturn(Optional.of(deck));

        // when
        handler.handle(queryWithSpecificId);

        // then
        verify(deckRepositoryAdapter).findById(eq(42L));
    }

    @Test
    @DisplayName("should throw exception for non-existent deck ID")
    void shouldThrowException_WhenDeckIdDoesNotExist() {
        // given
        Long nonExistentId = 999L;
        GetDeckQuery queryWithBadId = new GetDeckQuery(nonExistentId);
        when(deckRepositoryAdapter.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> handler.handle(queryWithBadId))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 999");

        verify(deckRepositoryAdapter).findById(nonExistentId);
    }

    @Test
    @DisplayName("should preserve all deck fields when returning")
    void shouldPreserveAllFields_WhenReturningDeck() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 15, 10, 30);
        Deck deckWithAllFields = new Deck(5L, "Complete Deck", "Full Description", createdAt);
        GetDeckQuery q = new GetDeckQuery(5L);
        when(deckRepositoryAdapter.findById(5L)).thenReturn(Optional.of(deckWithAllFields));

        // when
        Deck result = handler.handle(q);

        // then
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Complete Deck");
        assertThat(result.getDescription()).isEqualTo("Full Description");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should invoke repository findById exactly once")
    void shouldInvokeFindByIdOnce_WhenHandlingQuery() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(testDeck));

        // when
        handler.handle(query);

        // then
        verify(deckRepositoryAdapter, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should handle deck with zero ID")
    void shouldHandleDeck_WithZeroId() {
        // given
        Long zeroId = 0L;
        GetDeckQuery queryWithZeroId = new GetDeckQuery(zeroId);
        Deck deckWithZeroId = new Deck(zeroId, "Deck", "Desc", LocalDateTime.now());
        when(deckRepositoryAdapter.findById(zeroId)).thenReturn(Optional.of(deckWithZeroId));

        // when
        Deck result = handler.handle(queryWithZeroId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should return deck with long name and description")
    void shouldReturnDeck_WithLongNameAndDescription() {
        // given
        String longName = "N".repeat(255);
        String longDesc = "D".repeat(1000);
        Deck deckWithLongValues = new Deck(1L, longName, longDesc, LocalDateTime.now());
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(deckWithLongValues));

        // when
        Deck result = handler.handle(query);

        // then
        assertThat(result.getName()).hasSize(255);
        assertThat(result.getDescription()).hasSize(1000);
    }
}
