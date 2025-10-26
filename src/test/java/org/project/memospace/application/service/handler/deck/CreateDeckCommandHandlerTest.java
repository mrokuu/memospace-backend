package org.project.memospace.application.service.handler.deck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.deck.CreateDeckCommand;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class CreateDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryPort deckRepository;

    @InjectMocks
    private CreateDeckCommandHandler handler;

    @Test
    void shouldCreateDeckSuccessfully() {
        // Given
        CreateDeckCommand command = new CreateDeckCommand("Spanish Vocabulary", "Basic Spanish words");
        Deck savedDeck = defaultDeck();

        when(deckRepository.save(any(Deck.class))).thenReturn(savedDeck);

        // When
        Deck result = handler.handle(command);

        // Then
        assertNotNull(result);
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void shouldPassCorrectDataToRepository() {
        // Given
        String expectedName = "Test Deck";
        String expectedDescription = "Test Description";
        CreateDeckCommand command = new CreateDeckCommand(expectedName, expectedDescription);

        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Deck result = handler.handle(command);

        // Then
        assertEquals(expectedName, result.name());
        assertEquals(expectedDescription, result.description());
    }

    @Test
    void shouldCreateDeckWithEmptyDescription() {
        // Given
        CreateDeckCommand command = new CreateDeckCommand("Deck Name", "");
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Deck result = handler.handle(command);

        // Then
        assertEquals("Deck Name", result.name());
        assertEquals("", result.description());
    }

    @Test
    void shouldCreateDeckWithNullDescription() {
        // Given
        CreateDeckCommand command = new CreateDeckCommand("Deck Name", null);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Deck result = handler.handle(command);

        // Then
        assertEquals("Deck Name", result.name());
        assertNull(result.description());
    }
}
