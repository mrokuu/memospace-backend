package org.memospace.web.mapper;

import org.memospace.dto.CreateNoteDto;
import org.memospace.web.dto.CreateNoteRequest;
import org.memospace.web.dto.CreateNoteResponse;
import org.memospace.web.dto.NoteDto;
import org.memospace.model.Note;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class NoteWebMapper {

    public Note toDomain(CreateNoteRequest request) {
        Map<String, String> fieldValues = request.getFieldValues() != null ?
                request.getFieldValues() : Collections.emptyMap();
        Set<String> tags = request.getTags() != null ?
                request.getTags() : Collections.emptySet();

        return Note.create(
                request.getDeckId(),
                request.getNoteTypeId(),
                fieldValues,
                tags
        );
    }

    public NoteDto toDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.id());
        dto.setDeckId(note.deckId());
        dto.setNoteTypeId(note.noteTypeId());
        dto.setFieldValues(note.fieldValues());
        dto.setTags(note.tags());
        dto.setCreatedAt(note.createdAt());
        dto.setUpdatedAt(note.updatedAt());
        return dto;
    }

    public CreateNoteResponse toCreateResponse(CreateNoteDto result) {
        return new CreateNoteResponse(toDto(result.note()), result.cardCount());
    }
}