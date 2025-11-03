package org.project.memospace.application.service.importer;

import org.project.memospace.application.service.config.TxtImportConfig.TxtImportProperties;
import org.project.memospace.application.service.handler.note.NoteCardGenerationService;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.model.importer.DeduplicationStrategy;
import org.project.memospace.domain.model.importer.ImportJob;
import org.project.memospace.domain.model.importer.ImportJobStatus;
import org.project.memospace.domain.model.importer.TxtImportDuplicate;
import org.project.memospace.domain.model.importer.TxtImportError;
import org.project.memospace.domain.model.importer.TxtImportPreview;
import org.project.memospace.domain.model.importer.TxtImportResult;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.project.memospace.domain.port.ImportJobRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
import org.project.memospace.domain.service.DuplicateDetector;
import org.project.memospace.domain.service.importer.TxtLineParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TxtImportService {

    private static final String SOURCE_TYPE = "TXT";
    private static final String FRONT_FIELD = "Front";
    private static final String BACK_FIELD = "Back";
    private static final String DEFAULT_NOTE_TYPE_NAME = "Basic (Front/Back)";

    private final DeckRepositoryPort deckRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;
    private final ImportJobRepositoryPort importJobRepository;
    private final NoteCardGenerationService cardGenerationService;
    private final TxtImportProperties properties;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;
    private final DuplicateDetector duplicateDetector = new DuplicateDetector();

    public TxtImportService(DeckRepositoryPort deckRepository,
                            NoteTypeRepositoryPort noteTypeRepository,
                            NoteRepositoryPort noteRepository,
                            CardRepositoryPort cardRepository,
                            ImportJobRepositoryPort importJobRepository,
                            NoteCardGenerationService cardGenerationService,
                            TxtImportProperties properties,
                            Clock clock,
                            PlatformTransactionManager transactionManager) {
        this.deckRepository = deckRepository;
        this.noteTypeRepository = noteTypeRepository;
        this.noteRepository = noteRepository;
        this.cardRepository = cardRepository;
        this.importJobRepository = importJobRepository;
        this.cardGenerationService = cardGenerationService;
        this.properties = properties;
        this.clock = clock;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public TxtImportResult importFile(TxtImportCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        validateDeck(command.deckId());

        validateContentType(command.contentType());

        ResolvedNoteType resolvedNoteType = resolveNoteType(command);
        NoteType noteType = resolvedNoteType.noteType();

        DeduplicationStrategy deduplicationStrategy = command.deduplicationStrategy();
        Set<String> knownFronts = deduplicationStrategy == DeduplicationStrategy.FRONT_EQUALS
                ? loadExistingFronts(command.deckId())
                : new HashSet<>();
        Set<String> knownHashes = deduplicationStrategy == DeduplicationStrategy.NOTE_HASH
                ? loadExistingHashes(command.deckId(), noteType)
                : new HashSet<>();
        Set<String> noteTags = buildTags(command.tags());

        ParseSummary parseSummary = parseFile(command, noteType, knownFronts, knownHashes);

        Optional<ImportJob> existingJob = findExistingJob(command.requestId());
        if (existingJob.isPresent() && !command.force()) {
            ImportJob job = existingJob.get();
            if (job.fileChecksum() != null && job.fileChecksum().equals(parseSummary.fileChecksum)) {
                return buildResultFromExisting(job, noteType.getId(), command.deckId(), parseSummary.fileChecksum);
            }
            throw new IllegalArgumentException("Request ID already used with different payload. Set force=true to override.");
        }

        if (resolvedNoteType.created() && !command.dryRun()) {
            noteType = noteTypeRepository.save(noteType);
        }

        ImportJob jobRecord = buildInitialJob(command, noteType.getId(), parseSummary, deduplicationStrategy, existingJob);
        jobRecord = importJobRepository.save(jobRecord);

        int persistedNotes = 0;
        int persistedCards = 0;

        try {
            if (!command.dryRun()) {
                BatchAggregate aggregate = persistNotes(command, noteType, noteTags, parseSummary.acceptedRecords, parseSummary.errors);
                persistedNotes = aggregate.notes();
                persistedCards = aggregate.cards();
            }

            ImportJob completed = jobRecord.markCompleted(
                    LocalDateTime.now(clock),
                    parseSummary.totalLines,
                    parseSummary.acceptedRecords.size(),
                    parseSummary.skippedCount,
                    persistedNotes,
                    persistedCards,
                    parseSummary.errors.size()
            ).toBuilder()
                    .fileChecksum(parseSummary.fileChecksum)
                    .build();
            importJobRepository.save(completed);

            return TxtImportResult.builder()
                    .dryRun(command.dryRun())
                    .deckId(command.deckId())
                    .noteTypeId(noteType.getId())
                    .deduplicationStrategy(deduplicationStrategy)
                    .reusedPreviousImport(false)
                    .forced(command.force())
                    .fileChecksum(parseSummary.fileChecksum)
                    .totalLines(parseSummary.totalLines)
                    .parsed(parseSummary.acceptedRecords.size())
                    .skipped(parseSummary.skippedCount)
                    .createdNotes(command.dryRun() ? parseSummary.acceptedRecords.size() : persistedNotes)
                    .createdCards(command.dryRun() ? parseSummary.acceptedRecords.size() : persistedCards)
                    .duplicates(parseSummary.duplicates)
                    .errors(parseSummary.errors)
                    .previewSample(parseSummary.preview)
                    .build();
        } catch (RuntimeException ex) {
            ImportJob failed = jobRecord.markFailed(
                    LocalDateTime.now(clock),
                    ex.getMessage(),
                    parseSummary.totalLines,
                    parseSummary.acceptedRecords.size(),
                    parseSummary.skippedCount,
                    persistedNotes,
                    persistedCards,
                    parseSummary.errors.size()
            ).toBuilder()
                    .fileChecksum(parseSummary.fileChecksum)
                    .build();
            importJobRepository.save(failed);
            throw ex;
        }
    }

    private void validateDeck(Long deckId) {
        if (deckId == null || !deckRepository.existsById(deckId)) {
            throw new DeckNotFoundException(deckId);
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return;
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        if (!normalized.startsWith("text/plain")) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType + ". Expected text/plain");
        }
    }

    private ResolvedNoteType resolveNoteType(TxtImportCommand command) {
        if (command.noteTypeId() != null) {
            NoteType noteType = noteTypeRepository.findById(command.noteTypeId())
                    .orElseThrow(() -> new NoteTypeNotFoundException(command.noteTypeId()));
            ensureFrontBack(noteType);
            return new ResolvedNoteType(noteType, false);
        }

        Optional<NoteType> existingBasic = noteTypeRepository.findByNameIgnoreCase(DEFAULT_NOTE_TYPE_NAME);
        if (existingBasic.isPresent()) {
            NoteType noteType = existingBasic.get();
            ensureFrontBack(noteType);
            return new ResolvedNoteType(noteType, false);
        }

        if (!command.createMissingNoteType()) {
            throw new IllegalArgumentException("NoteType 'Basic (Front/Back)' not found and createMissingNoteType=false");
        }

        CardTemplate template = CardTemplate.create(
                "Card 1",
                "{{Front}}",
                "<div>{{Back}}</div>",
                false
        );
        NoteType noteType = NoteType.create(
                DEFAULT_NOTE_TYPE_NAME,
                List.of(FRONT_FIELD, BACK_FIELD),
                List.of(template),
                ".card { font-size: 1rem; }"
        );
        return new ResolvedNoteType(noteType, true);
    }

    private void ensureFrontBack(NoteType noteType) {
        if (!noteType.getFields().contains(FRONT_FIELD) || !noteType.getFields().contains(BACK_FIELD)) {
            throw new IllegalArgumentException("NoteType must contain fields 'Front' and 'Back'");
        }
        if (noteType.getTemplates().isEmpty()) {
            throw new IllegalArgumentException("NoteType must define at least one card template");
        }
    }

    private Set<String> loadExistingFronts(Long deckId) {
        return noteRepository.findByDeckId(deckId).stream()
                .map(note -> normalizeFront(note.getFieldValue(FRONT_FIELD)))
                .filter(s -> !s.isEmpty())
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    private Set<String> loadExistingHashes(Long deckId, NoteType noteType) {
        return noteRepository.findByDeckId(deckId).stream()
                .filter(note -> note.noteTypeId().equals(noteType.getId()))
                .map(note -> duplicateDetector.computeNoteHash(note, noteType))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    private Set<String> buildTags(List<String> requestedTags) {
        Set<String> tags = new LinkedHashSet<>();
        if (requestedTags != null) {
            for (String tag : requestedTags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }
        tags.add("import:txt:" + LocalDate.now(clock));
        return tags;
    }

    private ParseSummary parseFile(TxtImportCommand command,
                                   NoteType noteType,
                                   Set<String> knownFronts,
                                   Set<String> knownHashes) {
        if (command.fileSizeBytes() > properties.getMaxFileSizeMb() * 1024L * 1024L) {
            throw new IllegalArgumentException("Import file exceeds maximum size of " + properties.getMaxFileSizeMb() + " MB");
        }

        TxtLineParser lineParser = new TxtLineParser(command.allowEmptySide(), properties.getMaxLineLength());
        List<ParsedRecord> accepted = new ArrayList<>();
        List<TxtImportError> errors = new ArrayList<>();
        List<TxtImportDuplicate> duplicates = new ArrayList<>();
        List<TxtImportPreview> preview = new ArrayList<>();

        int skipped = 0;
        int totalLines = 0;

        MessageDigest digest = createDigest();
        Charset charset = command.charset() != null ? command.charset() : StandardCharsets.UTF_8;
        try (InputStream in = new BufferedInputStream(command.inputStreamSupplier().get());
             DigestInputStream digestStream = new DigestInputStream(in, digest);
             BufferedReader reader = new BufferedReader(new InputStreamReader(digestStream, charset))) {

            String rawLine;
            int lineNumber = 0;
            boolean firstLine = true;
            while ((rawLine = reader.readLine()) != null) {
                lineNumber++;
                totalLines++;

                if (lineNumber > properties.getMaxLines()) {
                    errors.add(new TxtImportError(lineNumber, "Maximum line limit exceeded (" + properties.getMaxLines() + ")"));
                    break;
                }

                TxtLineParser.ParseOutcome outcome = lineParser.parse(lineNumber, rawLine, firstLine);
                firstLine = false;

                switch (outcome.status()) {
                    case SKIPPED -> { /* ignore comments/blank lines */ }
                    case ERROR -> errors.add(new TxtImportError(lineNumber, outcome.message()));
                    case SUCCESS -> {
                        ParsedRecord record = new ParsedRecord(outcome.lineNumber(), outcome.front(), outcome.back());
                        if (isDuplicate(record, command.deduplicationStrategy(), knownFronts, knownHashes, noteType)) {
                            duplicates.add(new TxtImportDuplicate(lineNumber, command.deduplicationStrategy().value(), record.front(), record.back()));
                            skipped++;
                        } else {
                            accepted.add(record);
                            if (preview.size() < properties.getPreviewSampleSize()) {
                                preview.add(new TxtImportPreview(record.front(), record.back()));
                            }
                        }
                    }
                }
            }
        } catch (MalformedInputException ex) {
            throw new IllegalArgumentException("Import file is not valid " + charsetName(charset) + " text", ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read import file: " + ex.getMessage(), ex);
        }

        String checksum = toHex(digest.digest());

        return new ParseSummary(accepted, errors, duplicates, preview, skipped, totalLines, checksum);
    }

    private boolean isDuplicate(ParsedRecord record,
                                DeduplicationStrategy strategy,
                                Set<String> knownFronts,
                                Set<String> knownHashes,
                                NoteType noteType) {
        switch (strategy) {
            case NONE -> {
                return false;
            }
            case FRONT_EQUALS -> {
                String normalizedFront = normalizeFront(record.front());
                if (normalizedFront.isEmpty()) {
                    return false;
                }
                if (knownFronts.contains(normalizedFront)) {
                    return true;
                }
                knownFronts.add(normalizedFront);
                return false;
            }
            case NOTE_HASH -> {
                String hash = duplicateDetector.computeNoteHash(
                        noteType.getName(),
                        record.asFieldMap()
                );
                if (knownHashes.contains(hash)) {
                    return true;
                }
                knownHashes.add(hash);
                return false;
            }
            default -> throw new IllegalArgumentException("Unsupported deduplication strategy: " + strategy);
        }
    }

    private BatchAggregate persistNotes(TxtImportCommand command,
                                        NoteType noteType,
                                        Set<String> tags,
                                        List<ParsedRecord> acceptedRecords,
                                        List<TxtImportError> errors) {
        int notesPersisted = 0;
        int cardsPersisted = 0;
        int batchSize = properties.getBatchSize();
        for (int idx = 0; idx < acceptedRecords.size(); idx += batchSize) {
            int end = Math.min(idx + batchSize, acceptedRecords.size());
            List<ParsedRecord> batch = acceptedRecords.subList(idx, end);
            BatchResult result = persistBatch(command.deckId(), noteType, tags, batch, command.strict(), errors);
            notesPersisted += result.notes();
            cardsPersisted += result.cards();
        }
        return new BatchAggregate(notesPersisted, cardsPersisted);
    }

    private BatchResult persistBatch(Long deckId,
                                     NoteType noteType,
                                     Set<String> tags,
                                     List<ParsedRecord> batch,
                                     boolean strict,
                                     List<TxtImportError> errors) {
        try {
            return transactionTemplate.execute(status -> {
                List<Card> cardsToSave = new ArrayList<>();
                int notesCount = 0;

                for (ParsedRecord record : batch) {
                    Note note = Note.create(
                            deckId,
                            noteType.getId(),
                            record.asFieldMap(),
                            tags
                    );
                    Note savedNote = noteRepository.save(note);
                    notesCount++;
                    cardsToSave.addAll(cardGenerationService.generateCardsForNote(savedNote, noteType));
                }

                if (!cardsToSave.isEmpty()) {
                    cardRepository.saveAll(cardsToSave);
                }

                return new BatchResult(notesCount, cardsToSave.size());
            });
        } catch (RuntimeException ex) {
            if (strict) {
                throw ex;
            }
            errors.add(new TxtImportError(-1, "Failed to persist batch: " + ex.getMessage()));
            return new BatchResult(0, 0);
        }
    }

    private ImportJob buildInitialJob(TxtImportCommand command,
                                      UUID noteTypeId,
                                      ParseSummary summary,
                                      DeduplicationStrategy strategy,
                                      Optional<ImportJob> existingJob) {
        ImportJob.ImportJobBuilder builder = existingJob
                .map(job -> job.toBuilder()
                        .startedAt(LocalDateTime.now(clock))
                        .finishedAt(null)
                        .status(ImportJobStatus.STARTED)
                        .errorMessage(null))
                .orElse(ImportJob.builder()
                        .startedAt(LocalDateTime.now(clock))
                        .status(ImportJobStatus.STARTED));

        builder.requestId(command.requestId());
        builder.sourceType(SOURCE_TYPE);
        builder.deckId(command.deckId());
        builder.noteTypeId(noteTypeId);
        builder.dryRun(command.dryRun());
        builder.forced(command.force());
        builder.userId(command.userId());
        builder.deduplicationStrategy(strategy);
        builder.fileChecksum(summary.fileChecksum);
        builder.totalLines(summary.totalLines);
        builder.parsedLines(summary.acceptedRecords.size());
        builder.skippedLines(summary.skippedCount);
        builder.createdNotes(0);
        builder.createdCards(0);
        builder.errorCount(summary.errors.size());

        return builder.build();
    }

    private TxtImportResult buildResultFromExisting(ImportJob job,
                                                    UUID noteTypeId,
                                                    Long deckId,
                                                    String checksum) {
        return TxtImportResult.builder()
                .dryRun(job.dryRun())
                .deckId(deckId)
                .noteTypeId(noteTypeId)
                .deduplicationStrategy(job.deduplicationStrategy() != null ? job.deduplicationStrategy() : DeduplicationStrategy.FRONT_EQUALS)
                .reusedPreviousImport(true)
                .forced(job.forced())
                .fileChecksum(checksum)
                .totalLines(job.totalLines())
                .parsed(job.parsedLines())
                .skipped(job.skippedLines())
                .createdNotes(job.createdNotes())
                .createdCards(job.createdCards())
                .duplicates(List.of())
                .errors(List.of())
                .previewSample(List.of())
                .build();
    }

    private Optional<ImportJob> findExistingJob(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return Optional.empty();
        }
        return importJobRepository.findByRequestId(requestId);
    }

    private MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    private String normalizeFront(String value) {
        if (value == null) {
            return "";
        }
        return value.strip().toLowerCase(Locale.ROOT);
    }

    private String charsetName(Charset charset) {
        return charset != null ? charset.displayName() : StandardCharsets.UTF_8.displayName();
    }

    private record ResolvedNoteType(NoteType noteType, boolean created) {
    }

    private record ParsedRecord(int lineNumber, String front, String back) {
        public java.util.Map<String, String> asFieldMap() {
            return java.util.Map.of(FRONT_FIELD, front, BACK_FIELD, back);
        }
    }

    private record ParseSummary(List<ParsedRecord> acceptedRecords,
                                List<TxtImportError> errors,
                                List<TxtImportDuplicate> duplicates,
                                List<TxtImportPreview> preview,
                                int skippedCount,
                                int totalLines,
                                String fileChecksum) {
    }

    private record BatchResult(int notes, int cards) {
    }

    private record BatchAggregate(int notes, int cards) {
    }
}
