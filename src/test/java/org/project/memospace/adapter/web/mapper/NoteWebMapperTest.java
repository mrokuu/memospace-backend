package org.project.memospace.adapter.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.web.dto.CreateNoteDto;
import org.project.memospace.adapter.web.dto.CreateNoteRequest;
import org.project.memospace.adapter.web.dto.CreateNoteResponse;
import org.project.memospace.adapter.web.dto.NoteDto;
import org.project.memospace.domain.model.Note;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteWebMapperTest {

    private NoteWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoteWebMapper();
    }

    @Test
    void shouldMapCreateRequestToDomain() {
        // Given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setDeckId(1L);
        request.setNoteTypeId(UUID.randomUUID());
        request.setFieldValues(Map.of("Front", "Hello", "Back", "Hola"));
        request.setTags(Set.of("spanish", "greetings"));

        // When
        Note note = mapper.toDomain(request);

        // Then
        assertEquals(request.getDeckId(), note.deckId());
        assertEquals(request.getNoteTypeId(), note.noteTypeId());
        assertEquals(request.getFieldValues(), note.fieldValues());
        assertEquals(request.getTags(), note.tags());
    }

    @Test
    void shouldHandleNullFieldValuesInRequest() {
        // Given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setDeckId(1L);
        request.setNoteTypeId(UUID.randomUUID());
        request.setFieldValues(null);
        request.setTags(Set.of("test"));

        // When
        Note note = mapper.toDomain(request);

        // Then
        assertNotNull(note.fieldValues());
        assertTrue(note.fieldValues().isEmpty());
    }

    @Test
    void shouldHandleNullTagsInRequest() {
        // Given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setDeckId(1L);
        request.setNoteTypeId(UUID.randomUUID());
        request.setFieldValues(Map.of("Front", "Test"));
        request.setTags(null);

        // When
        Note note = mapper.toDomain(request);

        // Then
        assertNotNull(note.tags());
        assertTrue(note.tags().isEmpty());
    }

    @Test
    void shouldMapDomainToDto() {
        // Given
        UUID id = UUID.randomUUID();
        UUID noteTypeId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Note note = Note.builder()
                .id(id)
                .deckId(1L)
                .noteTypeId(noteTypeId)
                .fieldValues(Map.of("Front", "Question", "Back", "Answer"))
                .tags(Set.of("tag1", "tag2"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        NoteDto dto = mapper.toDto(note);

        // Then
        assertEquals(note.id(), dto.getId());
        assertEquals(note.deckId(), dto.getDeckId());
        assertEquals(note.noteTypeId(), dto.getNoteTypeId());
        assertEquals(note.fieldValues(), dto.getFieldValues());
        assertEquals(note.tags(), dto.getTags());
        assertEquals(note.createdAt(), dto.getCreatedAt());
        assertEquals(note.updatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldMapCreateNoteResponseWithCardCount() {
        // Given
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .deckId(1L)
                .noteTypeId(UUID.randomUUID())
                .fieldValues(Map.of("Front", "Test"))
                .tags(Set.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CreateNoteDto createNoteDto = new CreateNoteDto(note, 2);

        // When
        CreateNoteResponse response = mapper.toCreateResponse(createNoteDto);

        // Then
        assertNotNull(response.getNote());
        assertEquals(2, response.getCardCount());
        assertEquals(note.id(), response.getNote().getId());
    }

    @Test
    void shouldHandleEmptyFieldValues() {
        // Given
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .deckId(1L)
                .noteTypeId(UUID.randomUUID())
                .fieldValues(Map.of())
                .tags(Set.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteDto dto = mapper.toDto(note);

        // Then
        assertNotNull(dto.getFieldValues());
        assertTrue(dto.getFieldValues().isEmpty());
    }

    @Test
    void shouldHandleEmptyTags() {
        // Given
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .deckId(1L)
                .noteTypeId(UUID.randomUUID())
                .fieldValues(Map.of("Front", "Test"))
                .tags(Set.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteDto dto = mapper.toDto(note);

        // Then
        assertNotNull(dto.getTags());
        assertTrue(dto.getTags().isEmpty());
    }

    @Test
    void shouldPreserveMultipleFieldValues() {
        // Given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setDeckId(1L);
        request.setNoteTypeId(UUID.randomUUID());
        request.setFieldValues(Map.of(
                "Field1", "Value1",
                "Field2", "Value2",
                "Field3", "Value3"
        ));
        request.setTags(Set.of());

        // When
        Note note = mapper.toDomain(request);

        // Then
        assertEquals(3, note.fieldValues().size());
        assertEquals("Value1", note.fieldValues().get("Field1"));
        assertEquals("Value2", note.fieldValues().get("Field2"));
        assertEquals("Value3", note.fieldValues().get("Field3"));
    }

    @Test
    void shouldPreserveMultipleTags() {
        // Given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setDeckId(1L);
        request.setNoteTypeId(UUID.randomUUID());
        request.setFieldValues(Map.of("Front", "Test"));
        request.setTags(Set.of("tag1", "tag2", "tag3", "tag4"));

        // When
        Note note = mapper.toDomain(request);

        // Then
        assertEquals(4, note.tags().size());
        assertTrue(note.tags().contains("tag1"));
        assertTrue(note.tags().contains("tag4"));
    }
}
