package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.domain.model.*;
import org.project.memospace.domain.model.exportimport.ExportModel;
import org.project.memospace.domain.port.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private DeckRepositoryPort deckRepository;
    @Mock
    private NoteTypeRepositoryPort noteTypeRepository;
    @Mock
    private NoteRepositoryPort noteRepository;
    @Mock
    private CardRepositoryPort cardRepository;
    @Mock
    private MediaRepositoryPort mediaRepository;

    private ExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportService(
                deckRepository,
                noteTypeRepository,
                noteRepository,
                cardRepository,
                mediaRepository
        );
    }

    @Test
    void shouldExportEmptyCollection() {
        // Given
        when(deckRepository.findAll()).thenReturn(List.of());
        when(noteTypeRepository.findAll()).thenReturn(List.of());
        when(noteRepository.findAll(any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of());

        // When
        ExportModel result = exportService.exportAll(false);

        // Then
        assertNotNull(result);
        assertEquals(1, result.version());
        assertTrue(result.decks().isEmpty());
        assertTrue(result.noteTypes().isEmpty());
        assertTrue(result.notes().isEmpty());
        assertTrue(result.cards().isEmpty());
        assertTrue(result.mediaManifest().isEmpty());
        assertNotNull(result.exportedAt());
    }

    @Test
    void shouldExportWithMediaManifest() {
        // Given
        when(deckRepository.findAll()).thenReturn(List.of());
        when(noteTypeRepository.findAll()).thenReturn(List.of());
        when(noteRepository.findAll(null, null, null, 0, Integer.MAX_VALUE)).thenReturn(List.of());

        MediaAsset mediaAsset = new MediaAsset(
                MediaId.of("a".repeat(64)),
                "image.png",
                "image/png",
                "png",
                1024L,
                java.time.Instant.now(),
                0
        );
        when(mediaRepository.findAll()).thenReturn(List.of(mediaAsset));

        // When
        ExportModel result = exportService.exportAll(true);

        // Then
        assertEquals(1, result.mediaManifest().size());
        assertEquals(mediaAsset.getId().value(), result.mediaManifest().get(0).id());
        assertEquals("image.png", result.mediaManifest().get(0).originalFilename());
        assertEquals("image/png", result.mediaManifest().get(0).mimeType());
        assertEquals(1024L, result.mediaManifest().get(0).sizeBytes());

        verify(mediaRepository).findAll();
    }

    @Test
    void shouldNotIncludeMediaManifestWhenDisabled() {
        // Given
        when(deckRepository.findAll()).thenReturn(List.of());
        when(noteTypeRepository.findAll()).thenReturn(List.of());
        when(noteRepository.findAll(any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of());

        // When
        ExportModel result = exportService.exportAll(false);

        // Then
        assertTrue(result.mediaManifest().isEmpty());
        verify(mediaRepository, never()).findAll();
    }

    @Test
    void shouldThrowExceptionWhenDeckNotFound() {
        // Given
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> exportService.exportDeck(999L, false)
        );
        assertEquals("Deck not found: 999", exception.getMessage());
    }
}
