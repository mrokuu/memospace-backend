package org.memospace.service;

import org.memospace.model.*;
import org.memospace.model.exportimport.*;
import org.memospace.port.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain service for exporting flashcard collection data.
 * Pure domain logic with no framework dependencies.
 */
public class ExportService {
    private final DeckRepositoryPort deckRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;
    private final MediaRepositoryPort mediaRepository;

    public ExportService(DeckRepositoryPort deckRepository, NoteTypeRepositoryPort noteTypeRepository,
                         NoteRepositoryPort noteRepository, CardRepositoryPort cardRepository,
                         MediaRepositoryPort mediaRepository) {
        this.deckRepository = deckRepository;
        this.noteTypeRepository = noteTypeRepository;
        this.noteRepository = noteRepository;
        this.cardRepository = cardRepository;
        this.mediaRepository = mediaRepository;
    }

    /**
     * Exports the entire collection.
     *
     * @param includeMediaManifest whether to include media manifest
     * @return export model containing all data
     */
    public ExportModel exportAll(boolean includeMediaManifest) {
        List<Deck> decks = deckRepository.findAll();
        List<NoteType> noteTypes = noteTypeRepository.findAll();
        List<Note> notes = noteRepository.findAll(null, null, null, 0, Integer.MAX_VALUE);
        List<Card> cards = new ArrayList<>();

        // Collect all cards for all decks
        for (Deck deck : decks) {
            List<Card> deckCards = cardRepository.searchCards(deck.id(), null, null, 0, Integer.MAX_VALUE);
            cards.addAll(deckCards);
        }

        List<MediaAsset> mediaAssets = includeMediaManifest ? mediaRepository.findAll() : List.of();

        return buildExportModel(decks, noteTypes, notes, cards, mediaAssets);
    }

    /**
     * Exports a specific deck and its contents.
     *
     * @param deckId the deck ID
     * @param includeMediaManifest whether to include media manifest
     * @return export model containing deck data
     */
    public ExportModel exportDeck(Long deckId, boolean includeMediaManifest) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        List<Note> notes = noteRepository.findAll(deckId, null, null, 0, Integer.MAX_VALUE);
        List<Card> cards = cardRepository.searchCards(deckId, null, null, 0, Integer.MAX_VALUE);

        // Collect unique note types used by these notes
        List<NoteType> noteTypes = notes.stream()
                .map(Note::noteTypeId)
                .distinct()
                .map(noteTypeRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());

        List<MediaAsset> mediaAssets = includeMediaManifest ? mediaRepository.findAll() : List.of();

        return buildExportModel(List.of(deck), noteTypes, notes, cards, mediaAssets);
    }

    /**
     * Builds an ExportModel from domain entities.
     *
     * @param decks source decks
     * @param noteTypes source note types
     * @param notes source notes
     * @param cards source cards
     * @param mediaAssets source media assets
     * @return export model
     */
    private ExportModel buildExportModel(List<Deck> decks, List<NoteType> noteTypes, List<Note> notes,
                                          List<Card> cards, List<MediaAsset> mediaAssets) {
        List<ExportDeck> exportDecks = decks.stream()
                .map(this::toExportDeck)
                .collect(Collectors.toList());

        List<ExportNoteType> exportNoteTypes = noteTypes.stream()
                .map(this::toExportNoteType)
                .collect(Collectors.toList());

        List<ExportNote> exportNotes = notes.stream()
                .map(this::toExportNote)
                .collect(Collectors.toList());

        List<ExportCard> exportCards = cards.stream()
                .map(this::toExportCard)
                .collect(Collectors.toList());

        List<MediaManifestEntry> mediaManifest = mediaAssets.stream()
                .map(this::toMediaManifestEntry)
                .collect(Collectors.toList());

        return new ExportModel(
                1, // version
                Instant.now(),
                exportDecks,
                exportNoteTypes,
                exportNotes,
                exportCards,
                mediaManifest
        );
    }

    private ExportDeck toExportDeck(Deck deck) {
        return new ExportDeck(
                String.valueOf(deck.id()),
                deck.name(),
                deck.description(),
                deck.createdAt()
        );
    }

    private ExportNoteType toExportNoteType(NoteType noteType) {
        List<ExportCardTemplate> exportTemplates = noteType.getTemplates().stream()
                .map(this::toExportCardTemplate)
                .collect(Collectors.toList());

        return new ExportNoteType(
                noteType.getId().toString(),
                noteType.getName(),
                noteType.getFields(),
                exportTemplates,
                noteType.getCss(),
                noteType.getCreatedAt(),
                noteType.getUpdatedAt()
        );
    }

    private ExportCardTemplate toExportCardTemplate(CardTemplate template) {
        return new ExportCardTemplate(
                template.id().toString(),
                template.name(),
                template.frontTemplate(),
                template.backTemplate(),
                template.isCloze()
        );
    }

    private ExportNote toExportNote(Note note) {
        return new ExportNote(
                note.id().toString(),
                String.valueOf(note.deckId()),
                note.noteTypeId().toString(),
                note.fieldValues(),
                note.tags(),
                note.createdAt(),
                note.updatedAt()
        );
    }

    private ExportCard toExportCard(Card card) {
        return new ExportCard(
                String.valueOf(card.id()),
                String.valueOf(card.deckId()),
                card.noteId() != null ? card.noteId().toString() : null,
                card.templateId() != null ? card.templateId().toString() : null,
                card.front(),
                card.back(),
                "REVIEW", // type - simplified for export
                card.easeFactor(),
                card.intervalDays(),
                card.repetitions(),
                0, // lapses - not tracked in current model
                card.dueAt(),
                false, // suspended - not tracked in current model
                0, // flag - not tracked in current model
                0, // position - not tracked in current model
                card.createdAt(),
                card.updatedAt()
        );
    }

    private MediaManifestEntry toMediaManifestEntry(MediaAsset asset) {
        return new MediaManifestEntry(
                asset.getId().value(), // SHA-256 hex
                asset.getOriginalFilename(),
                asset.getMimeType(),
                asset.getSizeBytes()
        );
    }
}
