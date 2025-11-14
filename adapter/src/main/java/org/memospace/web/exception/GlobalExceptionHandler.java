package org.memospace.web.exception;

import org.memospace.exception.CardNotFoundException;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.exception.InvalidMediaException;
import org.memospace.exception.MediaNotFoundException;
import org.memospace.exception.FilteredDeckNotFoundException;
import org.memospace.exception.InvalidQueryException;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.exception.NoteTypeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeckNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleDeckNotFoundException(DeckNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "deck-not-found", "Deck Not Found");
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCardNotFoundException(CardNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "card-not-found", "Card Not Found");
    }

    @ExceptionHandler(NoteTypeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteTypeNotFoundException(NoteTypeNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "note-type-not-found", "Note Type Not Found");
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteNotFoundException(NoteNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "note-not-found", "Note Not Found");
    }

    @ExceptionHandler(FilteredDeckNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleFilteredDeckNotFoundException(FilteredDeckNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "filtered-deck-not-found", "Filtered Deck Not Found");
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ProblemDetail> handleInvalidQueryException(InvalidQueryException ex) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage(), "invalid-query", "Invalid Query");
        problemDetail.setProperty("position", ex.getPosition());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleMediaNotFoundException(MediaNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "media-not-found", "Media Not Found");
    }

    @ExceptionHandler(InvalidMediaException.class)
    public ResponseEntity<ProblemDetail> handleInvalidMediaException(InvalidMediaException ex) {
        HttpStatus status = determineMediaExceptionStatus(ex.getMessage());
        return buildErrorResponse(status, ex.getMessage(), "invalid-media", "Invalid Media");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "validation-failed",
                "Validation Failed"
        );

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("validationErrors", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "invalid-argument", "Invalid Argument");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "conflict", "Conflict");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        // Handle 404 cases for missing static resources
        if (ex.getMessage() != null && ex.getMessage().contains("No static resource")) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "The requested resource was not found", "not-found", "Resource Not Found");
        }

        // Handle all other exceptions as 500
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                "internal-error",
                "Internal Server Error"
        );
        problemDetail.setProperty("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    /**
     * Helper method to build a standard error response with ProblemDetail.
     *
     * @param status HTTP status code
     * @param detail detailed error message
     * @param problemType problem type identifier (will be appended to base URI)
     * @param title human-readable title
     * @return ResponseEntity with ProblemDetail body
     */
    private ResponseEntity<ProblemDetail> buildErrorResponse(HttpStatus status, String detail, String problemType, String title) {
        ProblemDetail problemDetail = createProblemDetail(status, detail, problemType, title);
        return ResponseEntity.status(status).body(problemDetail);
    }

    /**
     * Creates a ProblemDetail object with standard properties.
     *
     * @param status HTTP status code
     * @param detail detailed error message
     * @param problemType problem type identifier (will be appended to base URI)
     * @param title human-readable title
     * @return configured ProblemDetail
     */
    private ProblemDetail createProblemDetail(HttpStatus status, String detail, String problemType, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create("https://api.memospace.com/problems/" + problemType));
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Determines appropriate HTTP status for media-related exceptions based on error message.
     *
     * @param message exception message
     * @return appropriate HTTP status
     */
    private HttpStatus determineMediaExceptionStatus(String message) {
        if (message != null) {
            if (message.contains("exceeds maximum") || message.contains("too large")) {
                return HttpStatus.PAYLOAD_TOO_LARGE; // 413
            }
            if (message.contains("Unsupported media type")) {
                return HttpStatus.UNSUPPORTED_MEDIA_TYPE; // 415
            }
            if (message.contains("filename") || message.contains("sanitization")) {
                return HttpStatus.UNPROCESSABLE_ENTITY; // 422
            }
        }
        return HttpStatus.BAD_REQUEST; // 400
    }
}