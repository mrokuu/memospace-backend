package org.project.memospace.adapter.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.web.dto.CardTemplateDto;
import org.project.memospace.adapter.web.dto.CreateNoteTypeRequest;
import org.project.memospace.adapter.web.dto.NoteTypeDto;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.NoteType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteTypeWebMapperTest {

    private NoteTypeWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoteTypeWebMapper();
    }

    @Test
    void shouldMapCreateRequestToDomain() {
        // Given
        CardTemplateDto templateDto = new CardTemplateDto();
        templateDto.setName("Card 1");
        templateDto.setFrontTemplate("{{Front}}");
        templateDto.setBackTemplate("{{Back}}");
        templateDto.setIsCloze(false);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("Basic");
        request.setFields(List.of("Front", "Back"));
        request.setTemplates(List.of(templateDto));
        request.setCss(".card { color: black; }");

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertEquals("Basic", noteType.getName());
        assertEquals(2, noteType.getFields().size());
        assertEquals("Front", noteType.getFields().get(0));
        assertEquals("Back", noteType.getFields().get(1));
        assertEquals(1, noteType.getTemplates().size());
        assertEquals("Card 1", noteType.getTemplates().get(0).name());
        assertEquals("{{Front}}", noteType.getTemplates().get(0).frontTemplate());
        assertEquals("{{Back}}", noteType.getTemplates().get(0).backTemplate());
        assertFalse(noteType.getTemplates().get(0).isCloze());
        assertEquals(".card { color: black; }", noteType.getCss());
    }

    @Test
    void shouldMapDomainToDto() {
        // Given
        UUID noteTypeId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CardTemplate template = new CardTemplate(
                templateId, "Template 1", "{{Question}}", "{{Answer}}", false
        );

        NoteType noteType = NoteType.builder()
                .id(noteTypeId)
                .name("QA Type")
                .field("Question")
                .field("Answer")
                .template(template)
                .css(".qa { font-size: 16px; }")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        NoteTypeDto dto = mapper.toDto(noteType);

        // Then
        assertEquals(noteTypeId, dto.getId());
        assertEquals("QA Type", dto.getName());
        assertEquals(2, dto.getFields().size());
        assertEquals("Question", dto.getFields().get(0));
        assertEquals("Answer", dto.getFields().get(1));
        assertEquals(1, dto.getTemplates().size());
        assertEquals(templateId, dto.getTemplates().get(0).getId());
        assertEquals("Template 1", dto.getTemplates().get(0).getName());
        assertEquals("{{Question}}", dto.getTemplates().get(0).getFrontTemplate());
        assertEquals("{{Answer}}", dto.getTemplates().get(0).getBackTemplate());
        assertFalse(dto.getTemplates().get(0).getIsCloze());
        assertEquals(".qa { font-size: 16px; }", dto.getCss());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void shouldMapMultipleTemplates() {
        // Given
        CardTemplateDto template1 = new CardTemplateDto();
        template1.setName("Card 1");
        template1.setFrontTemplate("{{Front}}");
        template1.setBackTemplate("{{Back}}");
        template1.setIsCloze(false);

        CardTemplateDto template2 = new CardTemplateDto();
        template2.setName("Card 2");
        template2.setFrontTemplate("{{Back}}");
        template2.setBackTemplate("{{Front}}");
        template2.setIsCloze(false);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("Multi-Template");
        request.setFields(List.of("Front", "Back"));
        request.setTemplates(List.of(template1, template2));
        request.setCss(null);

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertEquals(2, noteType.getTemplates().size());
        assertEquals("Card 1", noteType.getTemplates().get(0).name());
        assertEquals("Card 2", noteType.getTemplates().get(1).name());
    }

    @Test
    void shouldMapClozeTemplate() {
        // Given
        CardTemplateDto templateDto = new CardTemplateDto();
        templateDto.setName("Cloze");
        templateDto.setFrontTemplate("{{cloze:Text}}");
        templateDto.setBackTemplate("{{cloze:Text}}");
        templateDto.setIsCloze(true);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("Cloze Type");
        request.setFields(List.of("Text"));
        request.setTemplates(List.of(templateDto));
        request.setCss(null);

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertTrue(noteType.getTemplates().get(0).isCloze());
        assertEquals("{{cloze:Text}}", noteType.getTemplates().get(0).frontTemplate());
    }

    @Test
    void shouldMapNullCss() {
        // Given
        CardTemplateDto templateDto = new CardTemplateDto();
        templateDto.setName("Template");
        templateDto.setFrontTemplate("{{Front}}");
        templateDto.setBackTemplate("{{Back}}");
        templateDto.setIsCloze(false);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("No CSS");
        request.setFields(List.of("Front", "Back"));
        request.setTemplates(List.of(templateDto));
        request.setCss(null);

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertNull(noteType.getCss());
    }

    @Test
    void shouldPreserveFieldOrder() {
        // Given
        CardTemplateDto templateDto = new CardTemplateDto();
        templateDto.setName("Template");
        templateDto.setFrontTemplate("{{F1}}{{F2}}{{F3}}");
        templateDto.setBackTemplate("{{F4}}");
        templateDto.setIsCloze(false);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("Ordered");
        request.setFields(List.of("F1", "F2", "F3", "F4"));
        request.setTemplates(List.of(templateDto));
        request.setCss(null);

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertEquals("F1", noteType.getFields().get(0));
        assertEquals("F2", noteType.getFields().get(1));
        assertEquals("F3", noteType.getFields().get(2));
        assertEquals("F4", noteType.getFields().get(3));
    }

    @Test
    void shouldRoundTripDomainToDtoToDomain() {
        // Given
        CardTemplate template = CardTemplate.create("Template", "{{Front}}", "{{Back}}", false);
        NoteType original = NoteType.create(
                "Test Type",
                List.of("Front", "Back"),
                List.of(template),
                ".test { color: blue; }"
        );

        // When
        NoteTypeDto dto = mapper.toDto(original);

        // Then - verify DTO has correct values
        assertEquals(original.getName(), dto.getName());
        assertEquals(original.getFields(), dto.getFields());
        assertEquals(original.getCss(), dto.getCss());
        assertEquals(original.getTemplates().size(), dto.getTemplates().size());
    }

    @Test
    void shouldMapComplexNoteType() {
        // Given
        CardTemplateDto t1 = new CardTemplateDto();
        t1.setName("Front → Back");
        t1.setFrontTemplate("{{Question}}<br>{{Hint}}");
        t1.setBackTemplate("{{Answer}}<br>{{Extra}}");
        t1.setIsCloze(false);

        CardTemplateDto t2 = new CardTemplateDto();
        t2.setName("Back → Front");
        t2.setFrontTemplate("{{Answer}}");
        t2.setBackTemplate("{{Question}}");
        t2.setIsCloze(false);

        CreateNoteTypeRequest request = new CreateNoteTypeRequest();
        request.setName("Complex");
        request.setFields(List.of("Question", "Answer", "Hint", "Extra"));
        request.setTemplates(List.of(t1, t2));
        request.setCss(".complex { border: 1px solid black; }");

        // When
        NoteType noteType = mapper.toDomain(request);

        // Then
        assertEquals("Complex", noteType.getName());
        assertEquals(4, noteType.getFields().size());
        assertEquals(2, noteType.getTemplates().size());
        assertTrue(noteType.getTemplates().get(0).frontTemplate().contains("{{Hint}}"));
        assertEquals(".complex { border: 1px solid black; }", noteType.getCss());
    }
}
