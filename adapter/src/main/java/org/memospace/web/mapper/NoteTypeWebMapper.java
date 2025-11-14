package org.memospace.web.mapper;

import org.memospace.web.dto.CardTemplateDto;
import org.memospace.web.dto.CreateNoteTypeRequest;
import org.memospace.web.dto.NoteTypeDto;
import org.memospace.model.CardTemplate;
import org.memospace.model.NoteType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoteTypeWebMapper {

    public NoteType toDomain(CreateNoteTypeRequest request) {
        List<CardTemplate> templates = request.getTemplates().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return NoteType.create(
                request.getName(),
                request.getFields(),
                templates,
                request.getCss()
        );
    }

    public NoteTypeDto toDto(NoteType noteType) {
        NoteTypeDto dto = new NoteTypeDto();
        dto.setId(noteType.getId());
        dto.setName(noteType.getName());
        dto.setFields(noteType.getFields());
        dto.setTemplates(noteType.getTemplates().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        dto.setCss(noteType.getCss());
        dto.setCreatedAt(noteType.getCreatedAt());
        dto.setUpdatedAt(noteType.getUpdatedAt());
        return dto;
    }

    private CardTemplate toDomain(CardTemplateDto dto) {
        return CardTemplate.create(
                dto.getName(),
                dto.getFrontTemplate(),
                dto.getBackTemplate(),
                dto.getIsCloze()
        );
    }

    private CardTemplateDto toDto(CardTemplate template) {
        CardTemplateDto dto = new CardTemplateDto();
        dto.setId(template.id());
        dto.setName(template.name());
        dto.setFrontTemplate(template.frontTemplate());
        dto.setBackTemplate(template.backTemplate());
        dto.setIsCloze(template.isCloze());
        return dto;
    }
}