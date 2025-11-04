package org.project.memospace.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("User already exists with username: " + username);
    }

    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}
