package org.project.deckservice.application.handler.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.command.CreateDeckCommand;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDeckCommandHandler Unit Tests")
class CreateDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @InjectMocks
    private CreateDeckCommandHandler handler;

    private CreateDeckCommand command;
    private Deck savedDeck;

    @BeforeEach
    void setUp() {
        command = new CreateDeckCommand("Test Deck", "Test Description");
        savedDeck = new Deck(1L, "Test Deck", "Test Description", LocalDateTime.now());
    }

    @Test
    @DisplayName("should create deck successfully with name and description")
    void shouldCreateDeck_WhenValidCommandProvided() {
        // given
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(savedDeck);

        // when
        Deck result = handler.handle(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deck");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should create deck with null description")
    void shouldCreateDeck_WhenDescriptionIsNull() {
        // given
        CreateDeckCommand commandWithNullDesc = new CreateDeckCommand("Deck Name", null);
        Deck deckWithNullDesc = new Deck(2L, "Deck Name", null, LocalDateTime.now());
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithNullDesc);

        // when
        Deck result = handler.handle(commandWithNullDesc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Deck Name");
        assertThat(result.getDescription()).isNull();
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should create deck with empty description")
    void shouldCreateDeck_WhenDescriptionIsEmpty() {
        // given
        CreateDeckCommand commandWithEmptyDesc = new CreateDeckCommand("Deck Name", "");
        Deck deckWithEmptyDesc = new Deck(3L, "Deck Name", "", LocalDateTime.now());
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithEmptyDesc);

        // when
        Deck result = handler.handle(commandWithEmptyDesc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Deck Name");
        assertThat(result.getDescription()).isEmpty();
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should pass deck domain object to repository for saving")
    void shouldPassDeckToRepository_WhenHandlingCommand() {
        // given
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(savedDeck);

        // when
        handler.handle(command);

        // then
        verify(deckRepositoryAdapter).save(argThat(deck ->
            deck.getName().equals("Test Deck") &&
            deck.getDescription().equals("Test Description") &&
            deck.getId() == null &&
            deck.getCreatedAt() != null
        ));
    }

    @Test
    @DisplayName("should return saved deck from repository")
    void shouldReturnSavedDeck_WhenRepositorySaveSucceeds() {
        // given
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        Deck expectedDeck = new Deck(42L, "Test Deck", "Test Description", fixedTime);
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(expectedDeck);

        // when
        Deck result = handler.handle(command);

        // then
        assertThat(result).isEqualTo(expectedDeck);
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getCreatedAt()).isEqualTo(fixedTime);
    }

    @Test
    @DisplayName("should create deck with long name")
    void shouldCreateDeck_WhenNameIsLong() {
        // given
        String longName = "A".repeat(255);
        CreateDeckCommand commandWithLongName = new CreateDeckCommand(longName, "Description");
        Deck deckWithLongName = new Deck(4L, longName, "Description", LocalDateTime.now());
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithLongName);

        // when
        Deck result = handler.handle(commandWithLongName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).hasSize(255);
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should create deck with long description")
    void shouldCreateDeck_WhenDescriptionIsLong() {
        // given
        String longDescription = "B".repeat(1000);
        CreateDeckCommand commandWithLongDesc = new CreateDeckCommand("Name", longDescription);
        Deck deckWithLongDesc = new Deck(5L, "Name", longDescription, LocalDateTime.now());
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(deckWithLongDesc);

        // when
        Deck result = handler.handle(commandWithLongDesc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).hasSize(1000);
        verify(deckRepositoryAdapter).save(any(Deck.class));
    }

    @Test
    @DisplayName("should invoke repository save exactly once")
    void shouldInvokeRepositorySaveOnce_WhenHandlingCommand() {
        // given
        when(deckRepositoryAdapter.save(any(Deck.class))).thenReturn(savedDeck);

        // when
        handler.handle(command);

        // then
        verify(deckRepositoryAdapter, times(1)).save(any(Deck.class));
    }
}
