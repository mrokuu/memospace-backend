package org.project.deckservice.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.deckservice.adapter.web.dto.CreateDeckRequest;
import org.project.deckservice.adapter.web.dto.UpdateDeckRequest;
import org.project.deckservice.application.CommandBus;
import org.project.deckservice.application.QueryBus;
import org.project.deckservice.application.command.CreateDeckCommand;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.application.query.ListDecksQuery;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.project.deckservice.domain.model.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeckController.class)
@DisplayName("DeckController Integration Tests")
class DeckControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommandBus commandBus;

    @MockitoBean
    private QueryBus queryBus;

    @Test
    @DisplayName("should return 200 and list of decks when GET /api/v1/decks")
    void shouldReturnDecks_WhenGetAllDecks() throws Exception {
        // given
        Deck deck1 = new Deck(1L, "Deck 1", "Description 1", LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Deck 2", "Description 2", LocalDateTime.now());
        List<Deck> decks = Arrays.asList(deck1, deck2);
        when(queryBus.send(any(ListDecksQuery.class))).thenReturn(decks);

        // when & then
        mockMvc.perform(get("/api/v1/decks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Deck 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Deck 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")));
    }

    @Test
    @DisplayName("should return 200 and empty list when no decks exist")
    void shouldReturnEmptyList_WhenNoDecksExist() throws Exception {
        // given
        when(queryBus.send(any(ListDecksQuery.class))).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/v1/decks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should return 201 and created deck when POST /api/v1/decks with valid request")
    void shouldCreateDeck_WhenValidRequest() throws Exception {
        // given
        CreateDeckRequest request = new CreateDeckRequest("New Deck", "New Description");
        Deck createdDeck = new Deck(1L, "New Deck", "New Description", LocalDateTime.now());
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(createdDeck);

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Deck")))
                .andExpect(jsonPath("$.description", is("New Description")));
    }

    @Test
    @DisplayName("should return 400 when POST /api/v1/decks with blank name")
    void shouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // given
        CreateDeckRequest request = new CreateDeckRequest("", "Description");

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when POST /api/v1/decks with null name")
    void shouldReturnBadRequest_WhenNameIsNull() throws Exception {
        // given
        String requestJson = "{\"description\": \"Description\"}";

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when POST /api/v1/decks with name exceeding 255 characters")
    void shouldReturnBadRequest_WhenNameTooLong() throws Exception {
        // given
        String longName = "A".repeat(256);
        CreateDeckRequest request = new CreateDeckRequest(longName, "Description");

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when POST /api/v1/decks with description exceeding 1000 characters")
    void shouldReturnBadRequest_WhenDescriptionTooLong() throws Exception {
        // given
        String longDescription = "B".repeat(1001);
        CreateDeckRequest request = new CreateDeckRequest("Name", longDescription);

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 201 when POST /api/v1/decks with null description")
    void shouldCreateDeck_WhenDescriptionIsNull() throws Exception {
        // given
        CreateDeckRequest request = new CreateDeckRequest("Deck Name", null);
        Deck createdDeck = new Deck(1L, "Deck Name", null, LocalDateTime.now());
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(createdDeck);

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Deck Name")))
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    @Test
    @DisplayName("should return 200 and deck when GET /api/v1/decks/{id} with existing ID")
    void shouldReturnDeck_WhenGetDeckById() throws Exception {
        // given
        Long deckId = 1L;
        Deck deck = new Deck(deckId, "Test Deck", "Test Description", LocalDateTime.now());
        when(queryBus.send(any(GetDeckQuery.class))).thenReturn(deck);

        // when & then
        mockMvc.perform(get("/api/v1/decks/{id}", deckId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deck")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    @DisplayName("should return 404 when GET /api/v1/decks/{id} with non-existent ID")
    void shouldReturnNotFound_WhenDeckDoesNotExist() throws Exception {
        // given
        Long deckId = 999L;
        when(queryBus.send(any(GetDeckQuery.class))).thenThrow(new DeckNotFoundException(deckId));

        // when & then
        mockMvc.perform(get("/api/v1/decks/{id}", deckId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 200 and updated deck when PATCH /api/v1/decks/{id} with valid request")
    void shouldUpdateDeck_WhenValidRequest() throws Exception {
        // given
        Long deckId = 1L;
        UpdateDeckRequest request = new UpdateDeckRequest("Updated Name", "Updated Description");
        Deck updatedDeck = new Deck(deckId, "Updated Name", "Updated Description", LocalDateTime.now());
        when(commandBus.send(any(UpdateDeckCommand.class))).thenReturn(updatedDeck);

        // when & then
        mockMvc.perform(patch("/api/v1/decks/{id}", deckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    @DisplayName("should return 404 when PATCH /api/v1/decks/{id} with non-existent ID")
    void shouldReturnNotFound_WhenUpdatingNonExistentDeck() throws Exception {
        // given
        Long deckId = 999L;
        UpdateDeckRequest request = new UpdateDeckRequest("Name", "Description");
        when(commandBus.send(any(UpdateDeckCommand.class))).thenThrow(new DeckNotFoundException(deckId));

        // when & then
        mockMvc.perform(patch("/api/v1/decks/{id}", deckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 when PATCH /api/v1/decks/{id} with blank name")
    void shouldReturnBadRequest_WhenUpdatingWithBlankName() throws Exception {
        // given
        Long deckId = 1L;
        UpdateDeckRequest request = new UpdateDeckRequest("", "Description");

        // when & then
        mockMvc.perform(patch("/api/v1/decks/{id}", deckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when PATCH /api/v1/decks/{id} with name exceeding 255 characters")
    void shouldReturnBadRequest_WhenUpdatingWithLongName() throws Exception {
        // given
        Long deckId = 1L;
        String longName = "C".repeat(256);
        UpdateDeckRequest request = new UpdateDeckRequest(longName, "Description");

        // when & then
        mockMvc.perform(patch("/api/v1/decks/{id}", deckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when PATCH /api/v1/decks/{id} with description exceeding 1000 characters")
    void shouldReturnBadRequest_WhenUpdatingWithLongDescription() throws Exception {
        // given
        Long deckId = 1L;
        String longDescription = "D".repeat(1001);
        UpdateDeckRequest request = new UpdateDeckRequest("Name", longDescription);

        // when & then
        mockMvc.perform(patch("/api/v1/decks/{id}", deckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 204 when DELETE /api/v1/decks/{id} with existing ID")
    void shouldDeleteDeck_WhenDeckExists() throws Exception {
        // given
        Long deckId = 1L;
        when(commandBus.send(any(DeleteDeckCommand.class))).thenReturn(null);

        // when & then
        mockMvc.perform(delete("/api/v1/decks/{id}", deckId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("should return 404 when DELETE /api/v1/decks/{id} with non-existent ID")
    void shouldReturnNotFound_WhenDeletingNonExistentDeck() throws Exception {
        // given
        Long deckId = 999L;
        when(commandBus.send(any(DeleteDeckCommand.class))).thenThrow(new DeckNotFoundException(deckId));

        // when & then
        mockMvc.perform(delete("/api/v1/decks/{id}", deckId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 when POST /api/v1/decks with invalid JSON")
    void shouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // given
        String invalidJson = "{\"name\": \"Test\", invalid}";

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should accept valid request with name at max length 255 characters")
    void shouldAcceptRequest_WhenNameIsMaxLength() throws Exception {
        // given
        String maxLengthName = "E".repeat(255);
        CreateDeckRequest request = new CreateDeckRequest(maxLengthName, "Description");
        Deck createdDeck = new Deck(1L, maxLengthName, "Description", LocalDateTime.now());
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(createdDeck);

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(maxLengthName)));
    }

    @Test
    @DisplayName("should accept valid request with description at max length 1000 characters")
    void shouldAcceptRequest_WhenDescriptionIsMaxLength() throws Exception {
        // given
        String maxLengthDescription = "F".repeat(1000);
        CreateDeckRequest request = new CreateDeckRequest("Name", maxLengthDescription);
        Deck createdDeck = new Deck(1L, "Name", maxLengthDescription, LocalDateTime.now());
        when(commandBus.send(any(CreateDeckCommand.class))).thenReturn(createdDeck);

        // when & then
        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(maxLengthDescription)));
    }
}
