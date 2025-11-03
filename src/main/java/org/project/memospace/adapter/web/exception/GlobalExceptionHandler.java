package org.project.memospace.adapter.web.exception;

import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.exception.InvalidMediaException;
import org.project.memospace.domain.exception.MediaNotFoundException;
import org.project.memospace.domain.exception.FilteredDeckNotFoundException;
import org.project.memospace.domain.exception.InvalidQueryException;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
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
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/deck-not-found"));
        problemDetail.setTitle("Deck Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCardNotFoundException(CardNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/card-not-found"));
        problemDetail.setTitle("Card Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(NoteTypeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteTypeNotFoundException(NoteTypeNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/note-type-not-found"));
        problemDetail.setTitle("Note Type Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteNotFoundException(NoteNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/note-not-found"));
        problemDetail.setTitle("Note Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(FilteredDeckNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleFilteredDeckNotFoundException(FilteredDeckNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/filtered-deck-not-found"));
        problemDetail.setTitle("Filtered Deck Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ProblemDetail> handleInvalidQueryException(InvalidQueryException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/invalid-query"));
        problemDetail.setTitle("Invalid Query");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("position", ex.getPosition());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/invalid-request"));
        problemDetail.setTitle("Invalid Request");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleMediaNotFoundException(MediaNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/media-not-found"));
        problemDetail.setTitle("Media Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(InvalidMediaException.class)
    public ResponseEntity<ProblemDetail> handleInvalidMediaException(InvalidMediaException ex) {
        HttpStatus status = determineMediaExceptionStatus(ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setType(URI.create("https://api.memospace.com/problems/invalid-media"));
        problemDetail.setTitle("Invalid Media");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status).body(problemDetail);
    }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/validation-failed"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());

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
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/invalid-argument"));
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/conflict"));
        problemDetail.setTitle("Conflict");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        // Handle 404 cases for missing static resources
        if (ex.getMessage() != null && ex.getMessage().contains("No static resource")) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "The requested resource was not found"
            );
            problemDetail.setType(URI.create("https://api.memospace.com/problems/not-found"));
            problemDetail.setTitle("Resource Not Found");
            problemDetail.setProperty("timestamp", Instant.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }

        // Handle all other exceptions as 500
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setType(URI.create("https://api.memospace.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
