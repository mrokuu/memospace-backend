package org.project.memospace.domain.service;

/**
 * Pure domain utility for sanitizing filenames.
 * Removes or replaces dangerous characters to prevent security issues.
 */
public class FilenameSanitizer {

    private static final int MAX_FILENAME_LENGTH = 255;
    private static final String ALLOWED_CHARS_PATTERN = "[^A-Za-z0-9._\\- ]";
    private static final String REPLACEMENT = "_";

    /**
     * Sanitizes a filename by:
     * - Removing path traversal attempts (../, ..\, etc.)
     * - Replacing disallowed characters with underscore
     * - Limiting length to 255 characters
     * - Preserving file extension
     *
     * @param filename original filename
     * @return sanitized filename safe for storage metadata
     * @throws IllegalArgumentException if filename is null/blank or becomes empty after sanitization
     */
    public String sanitize(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or blank");
        }

        // Remove any path components - extract just the filename
        int lastSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        String cleaned = lastSlash >= 0 ? filename.substring(lastSlash + 1) : filename;

        // Remove path traversal attempts
        cleaned = cleaned.replaceAll("\\.\\.", "");

        // Replace disallowed characters
        cleaned = cleaned.replaceAll(ALLOWED_CHARS_PATTERN, REPLACEMENT);

        // Remove leading/trailing dots, spaces, underscores
        cleaned = cleaned.replaceAll("^[._ ]+", "");
        cleaned = cleaned.replaceAll("[._ ]+$", "");

        // Ensure it's not empty after sanitization
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("Filename becomes empty after sanitization: " + filename);
        }

        // Limit length
        if (cleaned.length() > MAX_FILENAME_LENGTH) {
            // Try to preserve extension
            int lastDot = cleaned.lastIndexOf('.');
            if (lastDot > 0 && lastDot < cleaned.length() - 1) {
                String extension = cleaned.substring(lastDot);
                String base = cleaned.substring(0, Math.min(MAX_FILENAME_LENGTH - extension.length(), lastDot));
                cleaned = base + extension;
            } else {
                cleaned = cleaned.substring(0, MAX_FILENAME_LENGTH);
            }
        }

        return cleaned;
    }

    /**
     * Validates that a filename doesn't contain path traversal or dangerous patterns.
     *
     * @param filename filename to validate
     * @return true if safe, false otherwise
     */
    public boolean isSafe(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        // Check for path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }

        // Check for control characters
        if (filename.matches(".*[\\x00-\\x1F\\x7F].*")) {
            return false;
        }

        return true;
    }
}
