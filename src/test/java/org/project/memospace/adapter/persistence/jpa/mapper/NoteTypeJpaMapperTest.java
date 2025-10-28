package org.project.memospace.adapter.persistence.jpa.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.persistence.jpa.entity.CardTemplateEntity;
import org.project.memospace.adapter.persistence.jpa.entity.NoteTypeEntity;
import org.project.memospace.adapter.persistence.jpa.entity.NoteTypeFieldEntity;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.NoteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteTypeJpaMapperTest {

    private NoteTypeJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoteTypeJpaMapper();
    }

    @Test
    void shouldMapDomainToEntity() {
        // Given
        UUID noteTypeId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        NoteType domain = NoteType.builder()
                .id(noteTypeId)
                .name("Basic")
                .field("Front")
                .field("Back")
                .template(new CardTemplate(templateId, "Card 1", "{{Front}}", "{{Back}}", false))
                .css(".card { color: black; }")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(noteTypeId, entity.getId());
        assertEquals("Basic", entity.getName());
        assertEquals(".card { color: black; }", entity.getCss());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());

        // Check fields
        assertEquals(2, entity.getFields().size());
        assertEquals("Front", entity.getFields().get(0).getName());
        assertEquals(0, entity.getFields().get(0).getIdx());
        assertEquals("Back", entity.getFields().get(1).getName());
        assertEquals(1, entity.getFields().get(1).getIdx());

        // Check templates
        assertEquals(1, entity.getTemplates().size());
        assertEquals(templateId, entity.getTemplates().get(0).getId());
        assertEquals("Card 1", entity.getTemplates().get(0).getName());
        assertEquals("{{Front}}", entity.getTemplates().get(0).getFrontTemplate());
        assertEquals("{{Back}}", entity.getTemplates().get(0).getBackTemplate());
        assertFalse(entity.getTemplates().get(0).getIsCloze());
    }

    @Test
    void shouldMapEntityToDomain() {
        // Given
        UUID noteTypeId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 1, 2, 14, 30);

        NoteTypeEntity entity = new NoteTypeEntity();
        entity.setId(noteTypeId);
        entity.setName("Cloze");
        entity.setCss(".cloze { background: yellow; }");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // Add fields
        NoteTypeFieldEntity field1 = new NoteTypeFieldEntity();
        field1.setNoteType(entity);
        field1.setIdx(0);
        field1.setName("Text");

        NoteTypeFieldEntity field2 = new NoteTypeFieldEntity();
        field2.setNoteType(entity);
        field2.setIdx(1);
        field2.setName("Extra");

        entity.setFields(List.of(field1, field2));

        // Add template
        CardTemplateEntity template = new CardTemplateEntity();
        template.setId(templateId);
        template.setNoteType(entity);
        template.setName("Cloze Card");
        template.setFrontTemplate("{{cloze:Text}}");
        template.setBackTemplate("{{cloze:Text}}<br>{{Extra}}");
        template.setIsCloze(true);

        entity.setTemplates(List.of(template));

        // When
        NoteType domain = mapper.toDomain(entity);

        // Then
        assertEquals(noteTypeId, domain.getId());
        assertEquals("Cloze", domain.getName());
        assertEquals(".cloze { background: yellow; }", domain.getCss());
        assertEquals(now, domain.getCreatedAt());
        assertEquals(now, domain.getUpdatedAt());

        // Check fields
        assertEquals(2, domain.getFields().size());
        assertEquals("Text", domain.getFields().get(0));
        assertEquals("Extra", domain.getFields().get(1));

        // Check templates
        assertEquals(1, domain.getTemplates().size());
        assertEquals(templateId, domain.getTemplates().get(0).id());
        assertEquals("Cloze Card", domain.getTemplates().get(0).name());
        assertEquals("{{cloze:Text}}", domain.getTemplates().get(0).frontTemplate());
        assertEquals("{{cloze:Text}}<br>{{Extra}}", domain.getTemplates().get(0).backTemplate());
        assertTrue(domain.getTemplates().get(0).isCloze());
    }

    @Test
    void shouldMapNoteTypeWithMultipleFields() {
        // Given
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("Multi-Field")
                .field("Field1")
                .field("Field2")
                .field("Field3")
                .field("Field4")
                .template(new CardTemplate(UUID.randomUUID(), "T1", "{{Field1}}", "{{Field2}}", false))
                .css(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(4, entity.getFields().size());
        for (int i = 0; i < 4; i++) {
            assertEquals(i, entity.getFields().get(i).getIdx());
            assertEquals("Field" + (i + 1), entity.getFields().get(i).getName());
        }
    }

    @Test
    void shouldMapNoteTypeWithMultipleTemplates() {
        // Given
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("Multi-Template")
                .field("Front")
                .field("Back")
                .template(new CardTemplate(UUID.randomUUID(), "Card 1", "{{Front}}", "{{Back}}", false))
                .template(new CardTemplate(UUID.randomUUID(), "Card 2", "{{Back}}", "{{Front}}", false))
                .css(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(2, entity.getTemplates().size());
        assertEquals("Card 1", entity.getTemplates().get(0).getName());
        assertEquals("Card 2", entity.getTemplates().get(1).getName());
    }

    @Test
    void shouldPreserveFieldOrder() {
        // Given
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("Ordered")
                .field("Alpha")
                .field("Beta")
                .field("Gamma")
                .template(new CardTemplate(UUID.randomUUID(), "T1", "{{Alpha}}", "{{Beta}}", false))
                .css(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);
        NoteType roundTripped = mapper.toDomain(entity);

        // Then
        assertEquals("Alpha", roundTripped.getFields().get(0));
        assertEquals("Beta", roundTripped.getFields().get(1));
        assertEquals("Gamma", roundTripped.getFields().get(2));
    }

    @Test
    void shouldHandleUnorderedFieldEntities() {
        // Given
        UUID noteTypeId = UUID.randomUUID();
        NoteTypeEntity entity = new NoteTypeEntity();
        entity.setId(noteTypeId);
        entity.setName("Test");
        entity.setCss(null);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // Add fields in wrong order
        NoteTypeFieldEntity field2 = new NoteTypeFieldEntity();
        field2.setIdx(2);
        field2.setName("Third");

        NoteTypeFieldEntity field0 = new NoteTypeFieldEntity();
        field0.setIdx(0);
        field0.setName("First");

        NoteTypeFieldEntity field1 = new NoteTypeFieldEntity();
        field1.setIdx(1);
        field1.setName("Second");

        entity.setFields(new ArrayList<>(List.of(field2, field0, field1)));

        CardTemplateEntity template = new CardTemplateEntity();
        template.setId(UUID.randomUUID());
        template.setName("T");
        template.setFrontTemplate("{{First}}");
        template.setBackTemplate("{{Second}}");
        template.setIsCloze(false);
        entity.setTemplates(List.of(template));

        // When
        NoteType domain = mapper.toDomain(entity);

        // Then - should be sorted by idx
        assertEquals("First", domain.getFields().get(0));
        assertEquals("Second", domain.getFields().get(1));
        assertEquals("Third", domain.getFields().get(2));
    }

    @Test
    void shouldMapNoteTypeWithNullCss() {
        // Given
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("No CSS")
                .field("Front")
                .field("Back")
                .template(new CardTemplate(UUID.randomUUID(), "T1", "{{Front}}", "{{Back}}", false))
                .css(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);
        NoteType roundTripped = mapper.toDomain(entity);

        // Then
        assertNull(roundTripped.getCss());
    }

    @Test
    void shouldRoundTripDomainToEntityToDomain() {
        // Given
        UUID noteTypeId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 1, 15, 10, 0);

        NoteType original = NoteType.builder()
                .id(noteTypeId)
                .name("Round Trip Test")
                .field("Question")
                .field("Answer")
                .field("Hint")
                .template(new CardTemplate(templateId, "QA", "{{Question}}{{Hint}}", "{{Answer}}", false))
                .css(".card { font-size: 16px; }")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(original);
        NoteType roundTripped = mapper.toDomain(entity);

        // Then
        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getName(), roundTripped.getName());
        assertEquals(original.getFields(), roundTripped.getFields());
        assertEquals(original.getCss(), roundTripped.getCss());
        assertEquals(original.getCreatedAt(), roundTripped.getCreatedAt());
        assertEquals(original.getUpdatedAt(), roundTripped.getUpdatedAt());

        assertEquals(original.getTemplates().size(), roundTripped.getTemplates().size());
        assertEquals(original.getTemplates().get(0).id(), roundTripped.getTemplates().get(0).id());
        assertEquals(original.getTemplates().get(0).name(), roundTripped.getTemplates().get(0).name());
        assertEquals(original.getTemplates().get(0).frontTemplate(), roundTripped.getTemplates().get(0).frontTemplate());
        assertEquals(original.getTemplates().get(0).backTemplate(), roundTripped.getTemplates().get(0).backTemplate());
        assertEquals(original.getTemplates().get(0).isCloze(), roundTripped.getTemplates().get(0).isCloze());
    }

    @Test
    void shouldMapClozeNoteType() {
        // Given
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("Cloze Type")
                .field("Text")
                .template(new CardTemplate(UUID.randomUUID(), "Cloze", "{{cloze:Text}}", "{{cloze:Text}}", true))
                .css(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);
        NoteType roundTripped = mapper.toDomain(entity);

        // Then
        assertTrue(roundTripped.getTemplates().get(0).isCloze());
        assertEquals("{{cloze:Text}}", roundTripped.getTemplates().get(0).frontTemplate());
    }

    @Test
    void shouldMapNoteTypeWithLongCss() {
        // Given
        String longCss = ".card { " + "color: black; ".repeat(1000) + "}";
        NoteType domain = NoteType.builder()
                .id(UUID.randomUUID())
                .name("Long CSS")
                .field("Front")
                .field("Back")
                .template(new CardTemplate(UUID.randomUUID(), "T1", "{{Front}}", "{{Back}}", false))
                .css(longCss)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        NoteTypeEntity entity = mapper.toEntity(domain);
        NoteType roundTripped = mapper.toDomain(entity);

        // Then
        assertTrue(roundTripped.getCss().length() > 10000);
        assertEquals(longCss, roundTripped.getCss());
    }
}
