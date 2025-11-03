package org.project.memospace.domain.service.importer;

import java.util.Optional;

public class TxtLineParser {

    private final boolean allowEmptySide;
    private final int maxLineLength;

    public TxtLineParser(boolean allowEmptySide, int maxLineLength) {
        this.allowEmptySide = allowEmptySide;
        this.maxLineLength = maxLineLength;
    }

    public ParseOutcome parse(int lineNumber, String rawLine, boolean stripBom) {
        if (rawLine == null) {
            return ParseOutcome.skip();
        }

        String line = stripBom ? stripBom(rawLine) : rawLine;

        if (line.length() > maxLineLength) {
            return ParseOutcome.error(lineNumber, "Line exceeds maximum length of " + maxLineLength + " characters");
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return ParseOutcome.skip();
        }

        int delimiterIndex = line.indexOf('|');
        if (delimiterIndex < 0) {
            return ParseOutcome.error(lineNumber, "Missing pipe delimiter");
        }

        String front = line.substring(0, delimiterIndex).strip();
        String back = line.substring(delimiterIndex + 1).strip();

        if (!allowEmptySide) {
            if (front.isEmpty() || back.isEmpty()) {
                return ParseOutcome.error(lineNumber, "Front and back values cannot be empty");
            }
        }

        return ParseOutcome.success(lineNumber, front, back);
    }

    private String stripBom(String line) {
        if (line.isEmpty()) {
            return line;
        }
        if (line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    public record ParseOutcome(Status status, int lineNumber, String front, String back, String message) {

        public static ParseOutcome skip() {
            return new ParseOutcome(Status.SKIPPED, 0, null, null, null);
        }

        public static ParseOutcome success(int lineNumber, String front, String back) {
            return new ParseOutcome(Status.SUCCESS, lineNumber, front, back, null);
        }

        public static ParseOutcome error(int lineNumber, String message) {
            return new ParseOutcome(Status.ERROR, lineNumber, null, null, message);
        }

        public Optional<ParsedLine> toParsedLine() {
            if (status == Status.SUCCESS) {
                return Optional.of(new ParsedLine(lineNumber, front, back));
            }
            return Optional.empty();
        }
    }

    public enum Status {
        SUCCESS,
        ERROR,
        SKIPPED
    }

    public record ParsedLine(int lineNumber, String front, String back) {
    }
}
