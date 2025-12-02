package org.project.deckservice.application.handler.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.query.ListDecksQuery;
import org.project.deckservice.domain.model.Deck;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListDecksQueryHandler Unit Tests")
class ListDecksQueryHandlerTest {

    @Mock
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @InjectMocks
    private ListDecksQueryHandler handler;

    private ListDecksQuery query;

    @BeforeEach
    void setUp() {
        query = new ListDecksQuery();
    }

    @Test
    @DisplayName("should return all decks when multiple decks exist")
    void shouldReturnAllDecks_WhenMultipleDecksExist() {
        // given
        Deck deck1 = new Deck(1L, "Deck 1", "Description 1", LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Deck 2", "Description 2", LocalDateTime.now());
        Deck deck3 = new Deck(3L, "Deck 3", "Description 3", LocalDateTime.now());
        List<Deck> expectedDecks = Arrays.asList(deck1, deck2, deck3);
        when(deckRepositoryAdapter.findAll()).thenReturn(expectedDecks);

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(deck1, deck2, deck3);
        verify(deckRepositoryAdapter).findAll();
    }

    @Test
    @DisplayName("should return empty list when no decks exist")
    void shouldReturnEmptyList_WhenNoDecksExist() {
        // given
        when(deckRepositoryAdapter.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(deckRepositoryAdapter).findAll();
    }

    @Test
    @DisplayName("should return single deck when only one exists")
    void shouldReturnSingleDeck_WhenOnlyOneExists() {
        // given
        Deck singleDeck = new Deck(1L, "Only Deck", "Only Description", LocalDateTime.now());
        when(deckRepositoryAdapter.findAll()).thenReturn(Collections.singletonList(singleDeck));

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(singleDeck);
        verify(deckRepositoryAdapter).findAll();
    }

    @Test
    @DisplayName("should return decks with null descriptions")
    void shouldReturnDecks_WithNullDescriptions() {
        // given
        Deck deck1 = new Deck(1L, "Deck 1", null, LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Deck 2", "Description 2", LocalDateTime.now());
        List<Deck> decks = Arrays.asList(deck1, deck2);
        when(deckRepositoryAdapter.findAll()).thenReturn(decks);

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isNull();
        assertThat(result.get(1).getDescription()).isEqualTo("Description 2");
    }

    @Test
    @DisplayName("should return decks with empty descriptions")
    void shouldReturnDecks_WithEmptyDescriptions() {
        // given
        Deck deck1 = new Deck(1L, "Deck 1", "", LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Deck 2", "Description 2", LocalDateTime.now());
        List<Deck> decks = Arrays.asList(deck1, deck2);
        when(deckRepositoryAdapter.findAll()).thenReturn(decks);

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEmpty();
        assertThat(result.get(1).getDescription()).isEqualTo("Description 2");
    }

    @Test
    @DisplayName("should preserve deck order from repository")
    void shouldPreserveDeckOrder_FromRepository() {
        // given
        Deck deck1 = new Deck(1L, "First", "Desc", LocalDateTime.now());
        Deck deck2 = new Deck(2L, "Second", "Desc", LocalDateTime.now());
        Deck deck3 = new Deck(3L, "Third", "Desc", LocalDateTime.now());
        List<Deck> orderedDecks = Arrays.asList(deck3, deck1, deck2);
        when(deckRepositoryAdapter.findAll()).thenReturn(orderedDecks);

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).containsExactly(deck3, deck1, deck2);
    }

    @Test
    @DisplayName("should invoke repository findAll exactly once")
    void shouldInvokeFindAllOnce_WhenHandlingQuery() {
        // given
        when(deckRepositoryAdapter.findAll()).thenReturn(Collections.emptyList());

        // when
        handler.handle(query);

        // then
        verify(deckRepositoryAdapter, times(1)).findAll();
    }

    @Test
    @DisplayName("should return list with all deck properties preserved")
    void shouldReturnList_WithAllPropertiesPreserved() {
        // given
        LocalDateTime createdAt1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, 2, 1, 11, 0);
        Deck deck1 = new Deck(1L, "Name 1", "Desc 1", createdAt1);
        Deck deck2 = new Deck(2L, "Name 2", "Desc 2", createdAt2);
        when(deckRepositoryAdapter.findAll()).thenReturn(Arrays.asList(deck1, deck2));

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Name 1");
        assertThat(result.get(0).getDescription()).isEqualTo("Desc 1");
        assertThat(result.get(0).getCreatedAt()).isEqualTo(createdAt1);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Name 2");
        assertThat(result.get(1).getDescription()).isEqualTo("Desc 2");
        assertThat(result.get(1).getCreatedAt()).isEqualTo(createdAt2);
    }

    @Test
    @DisplayName("should handle large list of decks")
    void shouldHandleLargeList_OfDecks() {
        // given
        List<Deck> largeList = Arrays.asList(
            new Deck(1L, "Deck 1", "Desc", LocalDateTime.now()),
            new Deck(2L, "Deck 2", "Desc", LocalDateTime.now()),
            new Deck(3L, "Deck 3", "Desc", LocalDateTime.now()),
            new Deck(4L, "Deck 4", "Desc", LocalDateTime.now()),
            new Deck(5L, "Deck 5", "Desc", LocalDateTime.now()),
            new Deck(6L, "Deck 6", "Desc", LocalDateTime.now()),
            new Deck(7L, "Deck 7", "Desc", LocalDateTime.now()),
            new Deck(8L, "Deck 8", "Desc", LocalDateTime.now()),
            new Deck(9L, "Deck 9", "Desc", LocalDateTime.now()),
            new Deck(10L, "Deck 10", "Desc", LocalDateTime.now())
        );
        when(deckRepositoryAdapter.findAll()).thenReturn(largeList);

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).hasSize(10);
        assertThat(result).containsExactlyElementsOf(largeList);
    }

    @Test
    @DisplayName("should return decks with various creation dates")
    void shouldReturnDecks_WithVariousCreationDates() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime lastWeek = now.minusWeeks(1);
        Deck deck1 = new Deck(1L, "Deck 1", "Desc", now);
        Deck deck2 = new Deck(2L, "Deck 2", "Desc", yesterday);
        Deck deck3 = new Deck(3L, "Deck 3", "Desc", lastWeek);
        when(deckRepositoryAdapter.findAll()).thenReturn(Arrays.asList(deck1, deck2, deck3));

        // when
        List<Deck> result = handler.handle(query);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCreatedAt()).isEqualTo(now);
        assertThat(result.get(1).getCreatedAt()).isEqualTo(yesterday);
        assertThat(result.get(2).getCreatedAt()).isEqualTo(lastWeek);
    }
}
