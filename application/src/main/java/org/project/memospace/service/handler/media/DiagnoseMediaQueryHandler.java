package org.project.memospace.application.service.handler.media;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.media.DiagnoseMediaQuery;
import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.port.MediaRepositoryPort;
import org.project.memospace.domain.port.MediaStoragePort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.service.MediaReferenceExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiagnoseMediaQueryHandler implements QueryHandler<DiagnoseMediaQuery, DiagnoseMediaQueryHandler.DiagnosticResult> {

    private static final Logger log = LoggerFactory.getLogger(DiagnoseMediaQueryHandler.class);

    private final MediaStoragePort storagePort;
    private final MediaRepositoryPort repositoryPort;
    private final NoteRepositoryPort noteRepositoryPort;
    private final MediaReferenceExtractor referenceExtractor;

    @Override
    public DiagnosticResult handle(DiagnoseMediaQuery query) {
        log.info("Starting media diagnostics...");

        // Get all media IDs from storage (disk)
        Set<MediaId> idsOnDisk = new HashSet<>(storagePort.listAll());
        log.debug("Found {} files on disk", idsOnDisk.size());

        // Get all media assets from database
        List<MediaAsset> assetsInDb = repositoryPort.findAll();
        Set<MediaId> idsInDb = assetsInDb.stream()
                .map(MediaAsset::getId)
                .collect(Collectors.toSet());
        log.debug("Found {} assets in database", idsInDb.size());

        // Get all notes and extract media references
        // Use large page size to get all notes (pagination with very large limit)
        List<Note> allNotes = noteRepositoryPort.findAll(null, null, null, 0, Integer.MAX_VALUE);
        log.debug("Scanning {} notes for media references", allNotes.size());

        Set<String> allReferences = new HashSet<>();
        Map<UUID, Set<String>> noteReferences = new HashMap<>();

        for (Note note : allNotes) {
            Set<String> refs = referenceExtractor.extract(note.fieldValues());
            if (!refs.isEmpty()) {
                allReferences.addAll(refs);
                noteReferences.put(note.id(), refs);
            }
        }
        log.debug("Found {} unique references in notes", allReferences.size());

        // Separate references into IDs (64 hex chars) and filenames
        Set<MediaId> referencedIds = new HashSet<>();
        Set<String> referencedFilenames = new HashSet<>();

        for (String ref : allReferences) {
            if (ref.matches("^[a-f0-9]{64}$")) {
                referencedIds.add(MediaId.of(ref));
            } else {
                referencedFilenames.add(ref);
            }
        }

        // Build filename-to-ID mapping from database
        Map<String, MediaId> filenameToId = assetsInDb.stream()
                .collect(Collectors.toMap(
                        MediaAsset::getOriginalFilename,
                        MediaAsset::getId,
                        (id1, id2) -> id1 // In case of duplicate filenames, keep first
                ));

        // Resolve filename references to IDs
        for (String filename : referencedFilenames) {
            MediaId resolvedId = filenameToId.get(filename);
            if (resolvedId != null) {
                referencedIds.add(resolvedId);
            }
        }

        // 1. Missing on disk: in DB but not on disk
        List<MediaId> missingOnDisk = idsInDb.stream()
                .filter(id -> !idsOnDisk.contains(id))
                .collect(Collectors.toList());

        // 2. Orphans on disk: on disk but not in DB
        List<OrphanFile> orphansOnDisk = idsOnDisk.stream()
                .filter(id -> !idsInDb.contains(id))
                .map(id -> {
                    try {
                        long size = storagePort.size(id);
                        return new OrphanFile(id.value(), size);
                    } catch (Exception e) {
                        return new OrphanFile(id.value(), 0L);
                    }
                })
                .collect(Collectors.toList());

        // 3. Dangling references: notes reference non-existing media
        List<DanglingReference> danglingReferences = new ArrayList<>();
        for (Map.Entry<UUID, Set<String>> entry : noteReferences.entrySet()) {
            UUID noteId = entry.getKey();
            Set<String> refs = entry.getValue();

            for (String ref : refs) {
                MediaId resolvedId = null;

                if (ref.matches("^[a-f0-9]{64}$")) {
                    resolvedId = MediaId.of(ref);
                } else {
                    resolvedId = filenameToId.get(ref);
                }

                // Check if resolved ID exists
                if (resolvedId == null || !idsInDb.contains(resolvedId)) {
                    danglingReferences.add(new DanglingReference(noteId, ref));
                }
            }
        }

        // 4. Unused assets: in DB but not referenced by any note
        List<MediaAsset> unusedAssets = repositoryPort.findUnreferenced(referencedIds);
        List<MediaId> unusedAssetIds = unusedAssets.stream()
                .map(MediaAsset::getId)
                .collect(Collectors.toList());

        DiagnosticSummary summary = new DiagnosticSummary(
                missingOnDisk.size(),
                orphansOnDisk.size(),
                danglingReferences.size(),
                unusedAssetIds.size()
        );

        log.info("Media diagnostics complete: {}", summary);

        return new DiagnosticResult(summary, missingOnDisk, orphansOnDisk, danglingReferences, unusedAssetIds);
    }

    public static class DiagnosticResult {
        private final DiagnosticSummary summary;
        private final List<MediaId> missingOnDisk;
        private final List<OrphanFile> orphansOnDisk;
        private final List<DanglingReference> danglingReferences;
        private final List<MediaId> unusedAssets;

        public DiagnosticResult(
                DiagnosticSummary summary,
                List<MediaId> missingOnDisk,
                List<OrphanFile> orphansOnDisk,
                List<DanglingReference> danglingReferences,
                List<MediaId> unusedAssets) {
            this.summary = summary;
            this.missingOnDisk = missingOnDisk;
            this.orphansOnDisk = orphansOnDisk;
            this.danglingReferences = danglingReferences;
            this.unusedAssets = unusedAssets;
        }

        public DiagnosticSummary getSummary() {
            return summary;
        }

        public List<MediaId> getMissingOnDisk() {
            return missingOnDisk;
        }

        public List<OrphanFile> getOrphansOnDisk() {
            return orphansOnDisk;
        }

        public List<DanglingReference> getDanglingReferences() {
            return danglingReferences;
        }

        public List<MediaId> getUnusedAssets() {
            return unusedAssets;
        }
    }

    public static class DiagnosticSummary {
        private final int missingOnDisk;
        private final int orphansOnDisk;
        private final int danglingReferences;
        private final int unusedAssets;

        public DiagnosticSummary(int missingOnDisk, int orphansOnDisk, int danglingReferences, int unusedAssets) {
            this.missingOnDisk = missingOnDisk;
            this.orphansOnDisk = orphansOnDisk;
            this.danglingReferences = danglingReferences;
            this.unusedAssets = unusedAssets;
        }

        public int getMissingOnDisk() {
            return missingOnDisk;
        }

        public int getOrphansOnDisk() {
            return orphansOnDisk;
        }

        public int getDanglingReferences() {
            return danglingReferences;
        }

        public int getUnusedAssets() {
            return unusedAssets;
        }

        @Override
        public String toString() {
            return String.format("missing=%d, orphans=%d, dangling=%d, unused=%d",
                    missingOnDisk, orphansOnDisk, danglingReferences, unusedAssets);
        }
    }

    public static class OrphanFile {
        private final String path;
        private final long sizeBytes;

        public OrphanFile(String path, long sizeBytes) {
            this.path = path;
            this.sizeBytes = sizeBytes;
        }

        public String getPath() {
            return path;
        }

        public long getSizeBytes() {
            return sizeBytes;
        }
    }

    public static class DanglingReference {
        private final UUID noteId;
        private final String reference;

        public DanglingReference(UUID noteId, String reference) {
            this.noteId = noteId;
            this.reference = reference;
        }

        public UUID getNoteId() {
            return noteId;
        }

        public String getReference() {
            return reference;
        }
    }
}
