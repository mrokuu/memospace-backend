package org.project.memospace.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class NoteWorkflowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void completeNoteWorkflow_shouldWork() throws Exception {
        // First create a deck
        String deckPayload = """
                {
                    "name": "Test Deck",
                    "description": "Integration test deck"
                }
                """;

        String deckResponse = mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deckPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long deckId = objectMapper.readTree(deckResponse).get("id").asLong();

        // Create a basic note type
        Map<String, Object> basicNoteType = Map.of(
                "name", "Basic",
                "fields", List.of("Front", "Back"),
                "templates", List.of(
                        Map.of(
                                "name", "Card 1",
                                "frontTemplate", "{{Front}}",
                                "backTemplate", "{{Back}}",
                                "isCloze", false
                        )
                ),
                "css", ".cloze{font-weight:bold;}"
        );

        String noteTypeResponse = mockMvc.perform(post("/api/v1/note-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basicNoteType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value("Basic"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String noteTypeId = objectMapper.readTree(noteTypeResponse).get("id").asText();

        // Create a note
        Map<String, Object> createNoteRequest = Map.of(
                "deckId", deckId,
                "noteTypeId", noteTypeId,
                "fieldValues", Map.of(
                        "Front", "Hello",
                        "Back", "Cześć"
                ),
                "tags", Set.of("greetings", "polish")
        );

        String noteResponse = mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createNoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note.id").value(notNullValue()))
                .andExpect(jsonPath("$.cardCount").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String noteId = objectMapper.readTree(noteResponse).get("note").get("id").asText();

        // Verify note was created
        mockMvc.perform(get("/api/v1/notes/" + noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldValues.Front").value("Hello"))
                .andExpect(jsonPath("$.fieldValues.Back").value("Cześć"))
                .andExpect(jsonPath("$.tags", hasSize(2)));

        // Get cards generated for this note
        mockMvc.perform(get("/api/v1/notes/" + noteId + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].front").value("Hello"))
                .andExpect(jsonPath("$[0].back").value("Cześć"))
                .andExpect(jsonPath("$[0].noteId").value(noteId))
                .andExpect(jsonPath("$[0].templateId").value(notNullValue()));

        // Search notes
        mockMvc.perform(get("/api/v1/notes")
                        .param("q", "Hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // Delete note (should also delete cards)
        mockMvc.perform(delete("/api/v1/notes/" + noteId))
                .andExpect(status().isNoContent());

        // Verify note is deleted
        mockMvc.perform(get("/api/v1/notes/" + noteId))
                .andExpect(status().isNotFound());
    }

    @Test
    void clozeNoteWorkflow_shouldWork() throws Exception {
        // First create a deck
        String deckPayload = """
                {
                    "name": "Cloze Test Deck",
                    "description": "Cloze test deck"
                }
                """;

        String deckResponse = mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deckPayload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long deckId = objectMapper.readTree(deckResponse).get("id").asLong();

        // Create a cloze note type
        Map<String, Object> clozeNoteType = Map.of(
                "name", "Cloze",
                "fields", List.of("Text", "Extra"),
                "templates", List.of(
                        Map.of(
                                "name", "Cloze",
                                "frontTemplate", "{{cloze:Text}}<br>{{Extra}}",
                                "backTemplate", "{{Text}}<br>{{Extra}}",
                                "isCloze", true
                        )
                ),
                "css", ".cloze{font-weight:bold;}"
        );

        String noteTypeResponse = mockMvc.perform(post("/api/v1/note-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clozeNoteType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String noteTypeId = objectMapper.readTree(noteTypeResponse).get("id").asText();

        // Create a cloze note
        Map<String, Object> createNoteRequest = Map.of(
                "deckId", deckId,
                "noteTypeId", noteTypeId,
                "fieldValues", Map.of(
                        "Text", "The capital of {{c1::Poland}} is {{c2::Warsaw}}.",
                        "Extra", "Geography fact"
                ),
                "tags", Set.of("geography", "capitals")
        );

        String noteResponse = mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createNoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note.id").value(notNullValue()))
                .andExpect(jsonPath("$.cardCount").value(2)) // Should generate 2 cloze cards
                .andReturn()
                .getResponse()
                .getContentAsString();

        String noteId = objectMapper.readTree(noteResponse).get("note").get("id").asText();

        // Verify cards were generated with cloze deletions
        mockMvc.perform(get("/api/v1/notes/" + noteId + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clozeIndex").value(notNullValue()))
                .andExpect(jsonPath("$[1].clozeIndex").value(notNullValue()));
    }
}