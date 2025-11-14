package org.memospace.persistence.jpa.mapper;

import org.memospace.persistence.jpa.entity.CardTemplateEntity;
import org.memospace.persistence.jpa.entity.NoteTypeEntity;
import org.memospace.persistence.jpa.entity.NoteTypeFieldEntity;
import org.memospace.model.CardTemplate;
import org.memospace.model.NoteType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoteTypeJpaMapper {

    public NoteTypeEntity toEntity(NoteType noteType) {
        NoteTypeEntity entity = new NoteTypeEntity();
        entity.setId(noteType.getId());
        entity.setName(noteType.getName());
        entity.setCss(noteType.getCss());
        entity.setCreatedAt(noteType.getCreatedAt());
        entity.setUpdatedAt(noteType.getUpdatedAt());

        // Map fields
        List<NoteTypeFieldEntity> fieldEntities = new ArrayList<>();
        for (int i = 0; i < noteType.getFields().size(); i++) {
            NoteTypeFieldEntity fieldEntity = new NoteTypeFieldEntity();
            fieldEntity.setNoteType(entity);
            fieldEntity.setIdx(i);
            fieldEntity.setName(noteType.getFields().get(i));
            fieldEntities.add(fieldEntity);
        }
        entity.setFields(fieldEntities);

        // Map templates
        List<CardTemplateEntity> templateEntities = noteType.getTemplates().stream()
                .map(template -> {
                    CardTemplateEntity templateEntity = new CardTemplateEntity();
                    templateEntity.setId(template.id());
                    templateEntity.setNoteType(entity);
                    templateEntity.setName(template.name());
                    templateEntity.setFrontTemplate(template.frontTemplate());
                    templateEntity.setBackTemplate(template.backTemplate());
                    templateEntity.setIsCloze(template.isCloze());
                    return templateEntity;
                })
                .collect(Collectors.toList());
        entity.setTemplates(templateEntities);

        return entity;
    }

    public NoteType toDomain(NoteTypeEntity entity) {
        // Map fields (ordered by idx)
        List<String> fields = entity.getFields().stream()
                .sorted((f1, f2) -> f1.getIdx().compareTo(f2.getIdx()))
                .map(NoteTypeFieldEntity::getName)
                .collect(Collectors.toList());

        // Map templates
        List<CardTemplate> templates = entity.getTemplates().stream()
                .map(templateEntity -> new CardTemplate(
                        templateEntity.getId(),
                        templateEntity.getName(),
                        templateEntity.getFrontTemplate(),
                        templateEntity.getBackTemplate(),
                        templateEntity.getIsCloze()
                ))
                .collect(Collectors.toList());

        return new NoteType(
                entity.getId(),
                entity.getName(),
                fields,
                templates,
                entity.getCss(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}