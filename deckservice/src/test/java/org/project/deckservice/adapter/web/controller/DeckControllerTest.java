package org.project.deckservice.adapter.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.web.dto.CreateDeckRequest;
import org.project.deckservice.adapter.web.dto.UpdateDeckRequest;
import org.project.deckservice.application.CommandBus;
import org.project.deckservice.application.QueryBus;
import org.project.deckservice.application.command.CreateDeckCommand;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.application.query.ListDecksQuery;
import org.project.deckservice.domain.model.Deck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeckController Unit Tests")
class DeckControllerTest {

    @Mock
    private CommandBus commandBus;

    @Mock
    private QueryBus queryBus;

    @InjectMocks
    private DeckController deckController;

    private Deck testDeck;
    private CreateDeckRequest createRequest;
    private UpdateDeckRequest updateRequest;

    @BeforeEach
    void setUp() {
        testDeck = new Deck(1L, "Test Deck", "Test Description", LocalDateTime.now());
        createRequest = new CreateDeckRequest("New Deck", "New Description");
        updateRequest = new UpdateDeckRequest("Updated Deck", "Updated Description");
    }

    @Test
    @DisplayName("should return all decks when listDecks is called")
    void shouldReturnAllDecks_WhenListDecksIsCalled() {
        // given
        Deck deck1 = new Deck(1L, "Deck 1", "Description 1", LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Deck 2", "Description 2", LocalDateTime.now());
        List<Deck> expectedDecks = Arrays.asList(deck1, deck2);
        when(queryBus.send(any(ListDecksQuery.class))).thenReturn(expectedDecks);

        // when
        ResponseEntity<List<Deck>> response = deckController.listDecks();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(deck1, deck2);
        verify(queryBus).send(any(ListDecksQuery.class));
    }

    @Test
    @DisplayName("should return empty list when no decks exist")
    void shouldReturnEmptyList_WhenNoDecksExist() {
        // given
        when(queryBus.send(any(ListDecksQuery.class))).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<List<Deck>> response = deckController.listDecks();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        verify(queryBus).send(any(ListDecksQuery.class));
    }

    @Test
    @DisplayName("should create deck successfully with valid request")
    void shouldCreateDeck_WhenValidRequestProvided() {
        // given
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(testDeck);

        // when
        ResponseEntity<Deck> response = deckController.createDeck(createRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(testDeck);
        verify(commandBus).send(any(CreateDeckCommand.class));
    }

    @Test
    @DisplayName("should create deck with null description")
    void shouldCreateDeck_WhenDescriptionIsNull() {
        // given
        CreateDeckRequest requestWithNullDesc = new CreateDeckRequest("Deck Name", null);
        Deck deckWithNullDesc = new Deck(1L, "Deck Name", null, LocalDateTime.now());
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(deckWithNullDesc);

        // when
        ResponseEntity<Deck> response = deckController.createDeck(requestWithNullDesc);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isNull();
        verify(commandBus).send(any(CreateDeckCommand.class));
    }

    @Test
    @DisplayName("should get deck by ID successfully")
    void shouldGetDeck_WhenValidIdProvided() {
        // given
        Long deckId = 1L;
        when(queryBus.send(any(GetDeckQuery.class))).thenReturn(testDeck);

        // when
        ResponseEntity<Deck> response = deckController.getDeck(deckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(testDeck);
        verify(queryBus).send(any(GetDeckQuery.class));
    }

    @Test
    @DisplayName("should update deck successfully with valid request")
    void shouldUpdateDeck_WhenValidRequestProvided() {
        // given
        Long deckId = 1L;
        Deck updatedDeck = new Deck(deckId, "Updated Deck", "Updated Description", testDeck.getCreatedAt());
        when(commandBus.send(any(UpdateDeckCommand.class))).thenReturn(updatedDeck);

        // when
        ResponseEntity<Deck> response = deckController.updateDeck(deckId, updateRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Deck");
        assertThat(response.getBody().getDescription()).isEqualTo("Updated Description");
        verify(commandBus).send(any(UpdateDeckCommand.class));
    }

    @Test
    @DisplayName("should update deck with null description")
    void shouldUpdateDeck_WhenDescriptionIsNull() {
        // given
        Long deckId = 1L;
        UpdateDeckRequest requestWithNullDesc = new UpdateDeckRequest("Updated Name", null);
        Deck updatedDeck = new Deck(deckId, "Updated Name", null, testDeck.getCreatedAt());
        when(commandBus.send(any(UpdateDeckCommand.class))).thenReturn(updatedDeck);

        // when
        ResponseEntity<Deck> response = deckController.updateDeck(deckId, requestWithNullDesc);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isNull();
        verify(commandBus).send(any(UpdateDeckCommand.class));
    }

    @Test
    @DisplayName("should delete deck successfully")
    void shouldDeleteDeck_WhenValidIdProvided() {
        // given
        Long deckId = 1L;
        when(commandBus.send(any(DeleteDeckCommand.class))).thenReturn(null);

        // when
        ResponseEntity<Void> response = deckController.deleteDeck(deckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(commandBus).send(any(DeleteDeckCommand.class));
    }

    @Test
    @DisplayName("should send correct command with parameters when creating deck")
    void shouldSendCorrectCommand_WhenCreatingDeck() {
        // given
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(testDeck);

        // when
        deckController.createDeck(createRequest);

        // then
        verify(commandBus).send(argThat(command -> {
            CreateDeckCommand cmd = (CreateDeckCommand) command;
            return cmd.getName().equals("New Deck") &&
                   cmd.getDescription().equals("New Description");
        }));
    }

    @Test
    @DisplayName("should send correct command with parameters when updating deck")
    void shouldSendCorrectCommand_WhenUpdatingDeck() {
        // given
        Long deckId = 1L;
        Deck updatedDeck = new Deck(deckId, "Updated Deck", "Updated Description", testDeck.getCreatedAt());
        when(commandBus.send(any(UpdateDeckCommand.class))).thenReturn(updatedDeck);

        // when
        deckController.updateDeck(deckId, updateRequest);

        // then
        verify(commandBus).send(argThat(command -> {
            UpdateDeckCommand cmd = (UpdateDeckCommand) command;
            return cmd.getDeckId().equals(1L) &&
                   cmd.getName().equals("Updated Deck") &&
                   cmd.getDescription().equals("Updated Description");
        }));
    }

    @Test
    @DisplayName("should send correct query with deck ID when getting deck")
    void shouldSendCorrectQuery_WhenGettingDeck() {
        // given
        Long deckId = 42L;
        when(queryBus.send(any(GetDeckQuery.class))).thenReturn(testDeck);

        // when
        deckController.getDeck(deckId);

        // then
        verify(queryBus).send(argThat(query -> {
            GetDeckQuery q = (GetDeckQuery) query;
            return q.getDeckId().equals(42L);
        }));
    }

    @Test
    @DisplayName("should send correct command with deck ID when deleting deck")
    void shouldSendCorrectCommand_WhenDeletingDeck() {
        // given
        Long deckId = 99L;
        when(commandBus.send(any(DeleteDeckCommand.class))).thenReturn(null);

        // when
        deckController.deleteDeck(deckId);

        // then
        verify(commandBus).send(argThat(command -> {
            DeleteDeckCommand cmd = (DeleteDeckCommand) command;
            return cmd.getDeckId().equals(99L);
        }));
    }
}
