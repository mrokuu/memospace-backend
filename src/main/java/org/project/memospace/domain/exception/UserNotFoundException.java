package org.project.memospace.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("User not found with id: " + userId);
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }
}
