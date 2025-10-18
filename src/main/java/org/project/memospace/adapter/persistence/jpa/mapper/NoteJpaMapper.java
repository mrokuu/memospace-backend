package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.NoteEntity;
import org.project.memospace.adapter.persistence.jpa.entity.NoteFieldEntity;
import org.project.memospace.adapter.persistence.jpa.entity.NoteTagEntity;
import org.project.memospace.domain.model.Note;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NoteJpaMapper {

    public NoteEntity toEntity(Note note) {
        NoteEntity entity = new NoteEntity();
        entity.setId(note.id());
        entity.setDeckId(note.deckId());
        entity.setNoteTypeId(note.noteTypeId());
        entity.setCreatedAt(note.createdAt());
        entity.setUpdatedAt(note.updatedAt());

        // Map fields
        Set<NoteFieldEntity> fieldEntities = note.fieldValues().entrySet().stream()
                .map(entry -> {
                    NoteFieldEntity fieldEntity = new NoteFieldEntity();
                    fieldEntity.setNote(entity);
                    fieldEntity.setName(entry.getKey());
                    fieldEntity.setValue(entry.getValue());
                    return fieldEntity;
                })
                .collect(Collectors.toSet());
        entity.setFields(fieldEntities);

        // Map tags
        Set<NoteTagEntity> tagEntities = note.tags().stream()
                .map(tag -> {
                    NoteTagEntity tagEntity = new NoteTagEntity();
                    tagEntity.setNote(entity);
                    tagEntity.setTag(tag);
                    return tagEntity;
                })
                .collect(Collectors.toSet());
        entity.setTags(tagEntities);

        return entity;
    }

    public Note toDomain(NoteEntity entity) {
        // Map field values
        Map<String, String> fieldValues = entity.getFields().stream()
                .collect(Collectors.toMap(
                        NoteFieldEntity::getName,
                        field -> field.getValue() != null ? field.getValue() : "",
                        (existing, replacement) -> replacement,
                        HashMap::new
                ));

        // Map tags
        Set<String> tags = entity.getTags().stream()
                .map(NoteTagEntity::getTag)
                .collect(Collectors.toSet());

        return new Note(
                entity.getId(),
                entity.getDeckId(),
                entity.getNoteTypeId(),
                fieldValues,
                tags,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}