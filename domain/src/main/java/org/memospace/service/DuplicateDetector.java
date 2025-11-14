package org.memospace.service;

import org.memospace.model.Note;
import org.memospace.model.NoteType;
import org.memospace.model.exportimport.ExportNote;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain service for detecting duplicate notes based on content hashing.
 * Pure domain logic with no framework dependencies.
 */
public class DuplicateDetector {

    /**
     * Computes a stable content hash for a note.
     * Hash is based on normalized note type name and normalized field values.
     *
     * @param noteTypeName the name of the note type
     * @param fieldValues the field values of the note
     * @return SHA-256 hex hash string
     */
    public String computeNoteHash(String noteTypeName, Map<String, String> fieldValues) {
        if (noteTypeName == null || noteTypeName.isBlank()) {
            throw new IllegalArgumentException("NoteTypeName cannot be null or blank");
        }
        if (fieldValues == null) {
            throw new IllegalArgumentException("FieldValues cannot be null");
        }

        String normalized = normalizeForHashing(noteTypeName, fieldValues);
        return sha256Hex(normalized);
    }

    /**
     * Computes a stable content hash for a domain Note.
     *
     * @param note the note to hash
     * @param noteType the note type
     * @return SHA-256 hex hash string
     */
    public String computeNoteHash(Note note, NoteType noteType) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        if (noteType == null) {
            throw new IllegalArgumentException("NoteType cannot be null");
        }

        return computeNoteHash(noteType.getName(), note.fieldValues());
    }

    /**
     * Computes a stable content hash for an ExportNote.
     *
     * @param exportNote the export note
     * @param noteTypeName the note type name
     * @return SHA-256 hex hash string
     */
    public String computeNoteHash(ExportNote exportNote, String noteTypeName) {
        if (exportNote == null) {
            throw new IllegalArgumentException("ExportNote cannot be null");
        }
        if (noteTypeName == null || noteTypeName.isBlank()) {
            throw new IllegalArgumentException("NoteTypeName cannot be null or blank");
        }

        return computeNoteHash(noteTypeName, exportNote.fieldValues());
    }

    /**
     * Normalizes note type name and field values for hashing.
     * Ensures consistent hash regardless of whitespace or field order.
     *
     * Format: noteTypeName|field1:value1|field2:value2|...
     * Fields are sorted alphabetically by key.
     *
     * @param noteTypeName the note type name
     * @param fieldValues the field values
     * @return normalized string for hashing
     */
    private String normalizeForHashing(String noteTypeName, Map<String, String> fieldValues) {
        // Normalize note type name: lowercase, trim, collapse whitespace
        String normalizedNoteType = normalizeString(noteTypeName);

        // Sort fields by key for stable ordering
        List<String> sortedKeys = new ArrayList<>(fieldValues.keySet());
        Collections.sort(sortedKeys);

        // Build normalized field string
        String normalizedFields = sortedKeys.stream()
                .map(key -> {
                    String value = fieldValues.get(key);
                    String normalizedKey = normalizeString(key);
                    String normalizedValue = normalizeString(value != null ? value : "");
                    return normalizedKey + ":" + normalizedValue;
                })
                .collect(Collectors.joining("|"));

        return normalizedNoteType + "|" + normalizedFields;
    }

    /**
     * Normalizes a string for consistent hashing.
     * - Trims whitespace
     * - Converts to lowercase
     * - Collapses multiple whitespace/newlines into single space
     *
     * @param str the string to normalize
     * @return normalized string
     */
    private String normalizeString(String str) {
        if (str == null || str.isBlank()) {
            return "";
        }

        return str.trim()
                .toLowerCase()
                .replaceAll("\\s+", " "); // collapse all whitespace (including newlines) to single space
    }

    /**
     * Computes SHA-256 hash and returns as hex string.
     *
     * @param input the input string
     * @return SHA-256 hex hash
     */
    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Converts byte array to hex string.
     *
     * @param bytes the byte array
     * @return hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
