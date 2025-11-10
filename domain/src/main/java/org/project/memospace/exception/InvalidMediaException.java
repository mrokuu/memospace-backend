package org.project.memospace.domain.exception;

public class InvalidMediaException extends RuntimeException {
    public InvalidMediaException(String message) {
        super(message);
    }

    public InvalidMediaException(String message, Throwable cause) {
        super(message, cause);
    }
}
