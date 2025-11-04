package org.project.memospace.domain.port;

import org.project.memospace.domain.model.HashedPassword;

/**
 * Port for password encoding operations.
 * Keeps domain layer framework-independent.
 */
public interface PasswordEncoderPort {
    /**
     * Hash a plain text password.
     */
    HashedPassword encode(String plainPassword);

    /**
     * Verify a plain text password against a hashed password.
     */
    boolean matches(String plainPassword, HashedPassword hashedPassword);
}
