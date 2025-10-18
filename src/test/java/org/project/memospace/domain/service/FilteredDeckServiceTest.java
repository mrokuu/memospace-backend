package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.FilteredDeck;
import org.project.memospace.domain.model.FilteredDeckMembership;
import org.project.memospace.domain.model.FilteredMode;
import org.project.memospace.domain.model.ReturnPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FilteredDeckServiceTest {

    private FilteredDeckService service;
    private QueryLanguageService queryLanguageService;

    @BeforeEach
    void setUp() {
        queryLanguageService = new QueryLanguageService();
        service = new FilteredDeckService(queryLanguageService);
    }

    @Test
    void buildFromQuery_withReviewOnlyMode_filtersNewCards() {
        // Arrange
        Instant now = Instant.now();
        LocalDateTime nowLocal = LocalDateTime.now();

        List<Card> candidates = List.of(
                createCard(1L, 0, nowLocal.minusDays(1)), // New card
                createCard(2L, 1, nowLocal.minusDays(1)), // Reviewed card
                createCard(3L, 2, nowLocal.minusDays(1))  // Reviewed card
        );

        // Act
        FilteredDeckService.BuildPlan plan = service.buildFromQuery(
                "test query",
                10,
                FilteredMode.REVIEW_ONLY,
                candidates,
                now
        );

        // Assert
        assertEquals(2, plan.getTotal(), "Should exclude new cards (repetitions=0)");
    }

    @Test
    void buildFromQuery_withIncludeNewMode_includesAllCards() {
        // Arrange
        Instant now = Instant.now();
        LocalDateTime nowLocal = LocalDateTime.now();

        List<Card> candidates = List.of(
                createCard(1L, 0, nowLocal.minusDays(1)),
                createCard(2L, 1, nowLocal.minusDays(1)),
                createCard(3L, 2, nowLocal.minusDays(1))
        );

        // Act
        FilteredDeckService.BuildPlan plan = service.buildFromQuery(
                "test query",
                10,
                FilteredMode.INCLUDE_NEW,
                candidates,
                now
        );

        // Assert
        assertEquals(3, plan.getTotal(), "Should include all cards");
    }

    @Test
    void buildFromQuery_respectsLimit() {
        // Arrange
        Instant now = Instant.now();
        LocalDateTime nowLocal = LocalDateTime.now();

        List<Card> candidates = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            candidates.add(createCard((long) i, 1, nowLocal.minusDays(1)));
        }

        // Act
        FilteredDeckService.BuildPlan plan = service.buildFromQuery(
                "test query",
                10,
                FilteredMode.INCLUDE_NEW,
                candidates,
                now
        );

        // Assert
        assertEquals(10, plan.getTotal(), "Should limit results to specified count");
    }

    @Test
    void shouldAutoReturn_whenEmpty_returnsFalse() {
        // Arrange
        Instant now = Instant.now();
        FilteredDeck deck = createFilteredDeck(ReturnPolicy.WHEN_EMPTY, now);

        // Act
        boolean result = service.shouldAutoReturn(now, now.plusSeconds(60), deck);

        // Assert
        assertFalse(result, "WHEN_EMPTY policy should not auto-return based on time");
    }

    @Test
    void shouldAutoReturn_onDelete_returnsFalse() {
        // Arrange
        Instant now = Instant.now();
        FilteredDeck deck = createFilteredDeck(ReturnPolicy.ON_DELETE, now);

        // Act
        boolean result = service.shouldAutoReturn(now, now.plusSeconds(60), deck);

        // Assert
        assertFalse(result, "ON_DELETE policy should never auto-return");
    }

    @Test
    void shouldAutoReturn_atCutoff_returnsTrue() {
        // Arrange
        Instant now = Instant.now();
        Instant createdAt = now.minusSeconds(120);
        Instant cutoff = now.minusSeconds(60);
        FilteredDeck deck = createFilteredDeck(ReturnPolicy.AT_CUTOFF, createdAt);

        // Act
        boolean result = service.shouldAutoReturn(now, cutoff, deck);

        // Assert
        assertTrue(result, "AT_CUTOFF policy should return true after cutoff");
    }

    @Test
    void createMemberships_assignsCorrectPositions() {
        // Arrange
        Instant now = Instant.now();
        LocalDateTime nowLocal = LocalDateTime.now();
        List<Card> cards = List.of(
                createCard(1L, 1, nowLocal),
                createCard(2L, 1, nowLocal),
                createCard(3L, 1, nowLocal)
        );

        FilteredDeckService.BuildPlan plan = service.buildFromQuery(
                "test",
                10,
                FilteredMode.INCLUDE_NEW,
                cards,
                now
        );

        UUID filteredDeckId = UUID.randomUUID();

        // Act
        List<FilteredDeckMembership> memberships = service.createMemberships(plan, filteredDeckId, now);

        // Assert
        assertEquals(3, memberships.size());
        for (int i = 0; i < memberships.size(); i++) {
            assertEquals(i, memberships.get(i).position(), "Position should match index");
            assertEquals(filteredDeckId, memberships.get(i).filteredDeckId());
        }
    }

    private Card createCard(Long id, int repetitions, LocalDateTime dueAt) {
        return new Card(
                id,
                1L,
                "Front",
                "Back",
                List.of(),
                2.5,
                1,
                repetitions,
                dueAt,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                null
        );
    }

    private FilteredDeck createFilteredDeck(ReturnPolicy policy, Instant createdAt) {
        return new FilteredDeck(
                UUID.randomUUID(),
                "Test Deck",
                null,
                "test query",
                100,
                FilteredMode.INCLUDE_NEW,
                policy,
                createdAt,
                null,
                null
        );
    }
}
