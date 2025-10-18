package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.CreateNoteDto;
import org.project.memospace.adapter.web.dto.CreateNoteRequest;
import org.project.memospace.adapter.web.dto.CreateNoteResponse;
import org.project.memospace.adapter.web.dto.NoteDto;
import org.project.memospace.domain.model.Note;
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