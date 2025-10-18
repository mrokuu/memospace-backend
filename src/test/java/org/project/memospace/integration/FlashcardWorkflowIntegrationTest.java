package org.project.memospace.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FlashcardWorkflowIntegrationTest {

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
    void shouldCompleteFullFlashcardWorkflow() throws Exception {
        // 1. Create a deck
        CreateDeckRequest createDeckRequest = new CreateDeckRequest(
                "Spanish Vocabulary",
                "Basic Spanish words for beginners"
        );

        MvcResult createDeckResult = mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDeckRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spanish Vocabulary"))
                .andExpect(jsonPath("$.description").value("Basic Spanish words for beginners"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        DeckDto createdDeck = objectMapper.readValue(
                createDeckResult.getResponse().getContentAsString(),
                DeckDto.class
        );
        Long deckId = createdDeck.id();

        // 2. Create cards in the deck
        CreateCardRequest createCardRequest1 = new CreateCardRequest(
                deckId,
                "What is the Spanish word for 'hello'?",
                "Hola",
                List.of("greetings", "basic")
        );

        MvcResult createCardResult1 = mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.front").value("What is the Spanish word for 'hello'?"))
                .andExpect(jsonPath("$.back").value("Hola"))
                .andExpect(jsonPath("$.deckId").value(deckId))
                .andExpect(jsonPath("$.repetitions").value(0))
                .andExpect(jsonPath("$.easeFactor").value(2.5))
                .andReturn();

        CardDto createdCard1 = objectMapper.readValue(
                createCardResult1.getResponse().getContentAsString(),
                CardDto.class
        );

        CreateCardRequest createCardRequest2 = new CreateCardRequest(
                deckId,
                "What is the Spanish word for 'goodbye'?",
                "Adiós",
                List.of("greetings", "basic")
        );

        MvcResult createCardResult2 = mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardRequest2)))
                .andExpect(status().isCreated())
                .andReturn();

        CardDto createdCard2 = objectMapper.readValue(
                createCardResult2.getResponse().getContentAsString(),
                CardDto.class
        );

        // 3. Get next due cards (should return both cards as they're new)
        mockMvc.perform(get("/api/v1/reviews/next")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        createdCard1.id().intValue(),
                        createdCard2.id().intValue()
                )));

        // 4. Review the first card with good quality
        ReviewRequest goodReview = new ReviewRequest(4, 3000);

        MvcResult reviewResult1 = mockMvc.perform(post("/api/v1/reviews/{cardId}", createdCard1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repetitions").value(1))
                .andExpect(jsonPath("$.intervalDays").value(1))
                .andExpect(jsonPath("$.easeFactor").value(greaterThan(2.5)))
                .andReturn();

        // 5. Review the second card with poor quality
        ReviewRequest poorReview = new ReviewRequest(1, 5000);

        mockMvc.perform(post("/api/v1/reviews/{cardId}", createdCard2.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(poorReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repetitions").value(0)) // Should reset
                .andExpect(jsonPath("$.intervalDays").value(1))
                .andExpect(jsonPath("$.easeFactor").value(2.5)); // Should reset

        // 6. Get review history
        mockMvc.perform(get("/api/v1/reviews/history")
                        .param("deckId", deckId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].quality", containsInAnyOrder(4, 1)))
                .andExpect(jsonPath("$[*].cardId", containsInAnyOrder(
                        createdCard1.id().intValue(),
                        createdCard2.id().intValue()
                )));

        // 7. Search cards
        mockMvc.perform(get("/api/v1/cards")
                        .param("deckId", deckId.toString())
                        .param("search", "hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].front").value("What is the Spanish word for 'hello'?"));

        // 8. Update a card
        UpdateCardRequest updateCardRequest = new UpdateCardRequest(
                "What is the Spanish word for 'hello' (informal)?",
                "Hola",
                List.of("greetings", "basic", "informal")
        );

        mockMvc.perform(patch("/api/v1/cards/{id}", createdCard1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.front").value("What is the Spanish word for 'hello' (informal)?"))
                .andExpect(jsonPath("$.tags", containsInAnyOrder("greetings", "basic", "informal")));

        // 9. List all decks
        mockMvc.perform(get("/api/v1/decks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Spanish Vocabulary"));

        // 10. Delete a card
        mockMvc.perform(delete("/api/v1/cards/{id}", createdCard2.id()))
                .andExpect(status().isNoContent());

        // Verify card is deleted
        mockMvc.perform(get("/api/v1/cards/{id}", createdCard2.id()))
                .andExpect(status().isNotFound());

        // 11. Delete the deck (should cascade delete remaining card)
        mockMvc.perform(delete("/api/v1/decks/{id}", deckId))
                .andExpect(status().isNoContent());

        // Verify deck is deleted
        mockMvc.perform(get("/api/v1/decks/{id}", deckId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        // Test deck creation with invalid data
        CreateDeckRequest invalidDeck = new CreateDeckRequest("", "Valid description");

        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDeck)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://api.memospace.com/problems/validation-failed"))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists());

        // Test card creation with invalid quality in review
        CreateDeckRequest validDeck = new CreateDeckRequest("Test Deck", "Description");
        MvcResult deckResult = mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDeck)))
                .andExpect(status().isCreated())
                .andReturn();

        DeckDto deck = objectMapper.readValue(deckResult.getResponse().getContentAsString(), DeckDto.class);

        CreateCardRequest validCard = new CreateCardRequest(deck.id(), "Question", "Answer", List.of());
        MvcResult cardResult = mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCard)))
                .andExpect(status().isCreated())
                .andReturn();

        CardDto card = objectMapper.readValue(cardResult.getResponse().getContentAsString(), CardDto.class);

        // Test review with invalid quality
        ReviewRequest invalidReview = new ReviewRequest(5, 1000); // Quality > 4

        mockMvc.perform(post("/api/v1/reviews/{cardId}", card.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReview)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://api.memospace.com/problems/validation-failed"));
    }

    @Test
    void shouldHandleNotFoundErrors() throws Exception {
        // Test getting non-existent deck
        mockMvc.perform(get("/api/v1/decks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://api.memospace.com/problems/deck-not-found"))
                .andExpect(jsonPath("$.title").value("Deck Not Found"));

        // Test getting non-existent card
        mockMvc.perform(get("/api/v1/cards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://api.memospace.com/problems/card-not-found"));

        // Test reviewing non-existent card
        ReviewRequest review = new ReviewRequest(3, 1000);
        mockMvc.perform(post("/api/v1/reviews/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound());
    }
}