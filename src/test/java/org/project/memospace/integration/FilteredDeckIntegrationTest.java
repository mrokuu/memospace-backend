package org.project.memospace.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class FilteredDeckIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createFilteredDeck_shouldSucceed() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "name", "Test Filtered Deck",
                "query", "test query",
                "limit", 100,
                "mode", "INCLUDE_NEW",
                "returnPolicy", "WHEN_EMPTY"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filteredDeck.id").value(notNullValue()))
                .andExpect(jsonPath("$.filteredDeck.name").value("Test Filtered Deck"))
                .andExpect(jsonPath("$.filteredDeck.query").value("test query"))
                .andExpect(jsonPath("$.filteredDeck.limit").value(100))
                .andExpect(jsonPath("$.filteredDeck.mode").value("INCLUDE_NEW"))
                .andExpect(jsonPath("$.filteredDeck.returnPolicy").value("WHEN_EMPTY"))
                .andExpect(jsonPath("$.total").value(notNullValue()));
    }

    @Test
    void rebuildFilteredDeck_shouldSucceed() throws Exception {
        // Arrange - Create filtered deck first
        Map<String, Object> createRequest = Map.of(
                "name", "Test Filtered Deck",
                "query", "test query",
                "limit", 50,
                "mode", "REVIEW_ONLY",
                "returnPolicy", "ON_DELETE"
        );

        String createResponse = mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String filteredDeckId = objectMapper.readTree(createResponse)
                .get("filteredDeck").get("id").asText();

        // Act & Assert - Rebuild
        mockMvc.perform(post("/api/v1/filtered-decks/" + filteredDeckId + ":rebuild"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filteredDeck.id").value(filteredDeckId))
                .andExpect(jsonPath("$.filteredDeck.lastBuiltAt").value(notNullValue()))
                .andExpect(jsonPath("$.total").value(notNullValue()));
    }

    @Test
    void getFilteredDeck_shouldReturnDeck() throws Exception {
        // Arrange - Create filtered deck
        Map<String, Object> createRequest = Map.of(
                "name", "Retrievable Deck",
                "query", "retrievable query",
                "limit", 200,
                "mode", "INCLUDE_NEW",
                "returnPolicy", "AT_CUTOFF"
        );

        String createResponse = mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String filteredDeckId = objectMapper.readTree(createResponse)
                .get("filteredDeck").get("id").asText();

        // Act & Assert - Get deck
        mockMvc.perform(get("/api/v1/filtered-decks/" + filteredDeckId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filteredDeckId))
                .andExpect(jsonPath("$.name").value("Retrievable Deck"))
                .andExpect(jsonPath("$.query").value("retrievable query"))
                .andExpect(jsonPath("$.limit").value(200))
                .andExpect(jsonPath("$.mode").value("INCLUDE_NEW"))
                .andExpect(jsonPath("$.returnPolicy").value("AT_CUTOFF"));
    }

    @Test
    void deleteFilteredDeck_shouldSucceed() throws Exception {
        // Arrange - Create filtered deck
        Map<String, Object> createRequest = Map.of(
                "name", "Deletable Deck",
                "query", "deletable query",
                "limit", 150,
                "mode", "REVIEW_ONLY",
                "returnPolicy", "ON_DELETE"
        );

        String createResponse = mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String filteredDeckId = objectMapper.readTree(createResponse)
                .get("filteredDeck").get("id").asText();

        // Act & Assert - Delete deck
        mockMvc.perform(delete("/api/v1/filtered-decks/" + filteredDeckId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/filtered-decks/" + filteredDeckId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNextCards_shouldReturnEmptyListInitially() throws Exception {
        // Arrange - Create filtered deck
        Map<String, Object> createRequest = Map.of(
                "name", "Study Deck",
                "query", "study query",
                "limit", 20,
                "mode", "INCLUDE_NEW",
                "returnPolicy", "WHEN_EMPTY"
        );

        String createResponse = mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String filteredDeckId = objectMapper.readTree(createResponse)
                .get("filteredDeck").get("id").asText();

        // Act & Assert - Get next cards (should be empty since no cards exist)
        mockMvc.perform(get("/api/v1/filtered-decks/" + filteredDeckId + "/next")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createFilteredDeck_withInvalidLimit_shouldFail() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "name", "Invalid Deck",
                "query", "test query",
                "limit", 0, // Invalid limit
                "mode", "INCLUDE_NEW",
                "returnPolicy", "WHEN_EMPTY"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilteredDeck_withBlankQuery_shouldFail() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "name", "Invalid Deck",
                "query", "", // Blank query
                "limit", 100,
                "mode", "INCLUDE_NEW",
                "returnPolicy", "WHEN_EMPTY"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/filtered-decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
