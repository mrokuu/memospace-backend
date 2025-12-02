package org.project.deckservice.application.handler.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateDeckCommandHandler Unit Tests")
class UpdateDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @InjectMocks
    private UpdateDeckCommandHandler handler;

    private UpdateDeckCommand command;
    private Deck existingDeck;
    private Deck updatedDeck;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        command = new UpdateDeckCommand(1L, "Updated Name", "Updated Description");
        existingDeck = new Deck(1L, "Original Name", "Original Description", createdAt);
        updatedDeck = new Deck(1L, "Updated Name", "Updated Description", createdAt);
    }

    @Test
    @DisplayName("should update deck successfully when deck exists")
    void shouldUpdateDeck_WhenDeckExists() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(updatedDeck);

        // when
        Deck result = handler.handle(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        verify(deckRepositoryAdapter).findById(1L);
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should throw DeckNotFoundException when deck does not exist")
    void shouldThrowException_WhenDeckNotFound() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> handler.handle(command))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 1");

        verify(deckRepositoryAdapter).findById(1L);
        verify(deckRepositoryAdapter, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("should preserve created date when updating deck")
    void shouldPreserveCreatedDate_WhenUpdatingDeck() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(updatedDeck);

        // when
        Deck result = handler.handle(command);

        // then
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        verify(deckRepositoryAdapter).save(argThat(deck ->
            deck.getCreatedAt().equals(createdAt)
        ));
    }

    @Test
    @DisplayName("should update deck with null description")
    void shouldUpdateDeck_WhenDescriptionIsNull() {
        // given
        UpdateDeckCommand commandWithNullDesc = new UpdateDeckCommand(1L, "New Name", null);
        Deck deckWithNullDesc = new Deck(1L, "New Name", null, createdAt);
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithNullDesc);

        // when
        Deck result = handler.handle(commandWithNullDesc);

        // then
        assertThat(result.getDescription()).isNull();
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should update deck with empty description")
    void shouldUpdateDeck_WhenDescriptionIsEmpty() {
        // given
        UpdateDeckCommand commandWithEmptyDesc = new UpdateDeckCommand(1L, "New Name", "");
        Deck deckWithEmptyDesc = new Deck(1L, "New Name", "", createdAt);
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithEmptyDesc);

        // when
        Deck result = handler.handle(commandWithEmptyDesc);

        // then
        assertThat(result.getDescription()).isEmpty();
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should throw exception for non-existent deck ID")
    void shouldThrowException_WhenDeckIdDoesNotExist() {
        // given
        Long nonExistentId = 999L;
        UpdateDeckCommand cmdWithBadId = new UpdateDeckCommand(nonExistentId, "Name", "Desc");
        when(deckRepositoryAdapter.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> handler.handle(cmdWithBadId))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 999");

        verify(deckRepositoryAdapter).findById(nonExistentId);
    }

    @Test
    @DisplayName("should update only name and description, not ID")
    void shouldUpdateOnlyNameAndDescription_NotId() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(updatedDeck);

        // when
        Deck result = handler.handle(command);

        // then
        assertThat(result.getId()).isEqualTo(existingDeck.getId());
        verify(deckRepositoryAdapter).save(argThat(deck ->
            deck.getId().equals(1L) &&
            deck.getName().equals("Updated Name") &&
            deck.getDescription().equals("Updated Description")
        ));
    }

    @Test
    @DisplayName("should call repository findById with correct deck ID")
    void shouldCallFindById_WithCorrectDeckId() {
        // given
        Long deckId = 42L;
        UpdateDeckCommand cmd = new UpdateDeckCommand(deckId, "Name", "Desc");
        Deck deck = new Deck(deckId, "Old Name", "Old Desc", createdAt);
        when(deckRepositoryAdapter.findById(deckId)).thenReturn(Optional.of(deck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deck);

        // when
        handler.handle(cmd);

        // then
        verify(deckRepositoryAdapter).findById(eq(42L));
    }

    @Test
    @DisplayName("should invoke repository methods in correct order")
    void shouldInvokeRepositoryMethodsInOrder_WhenUpdating() {
        // given
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(updatedDeck);

        // when
        handler.handle(command);

        // then
        var inOrder = inOrder(deckRepositoryAdapter);
        inOrder.verify(deckRepositoryAdapter).findById(1L);
        inOrder.verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should update deck with long name and description")
    void shouldUpdateDeck_WhenNameAndDescriptionAreLong() {
        // given
        String longName = "N".repeat(255);
        String longDesc = "D".repeat(1000);
        UpdateDeckCommand cmdWithLongValues = new UpdateDeckCommand(1L, longName, longDesc);
        Deck deckWithLongValues = new Deck(1L, longName, longDesc, createdAt);
        when(deckRepositoryAdapter.findById(1L)).thenReturn(Optional.of(existingDeck));
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithLongValues);

        // when
        Deck result = handler.handle(cmdWithLongValues);

        // then
        assertThat(result.getName()).hasSize(255);
        assertThat(result.getDescription()).hasSize(1000);
    }
}
