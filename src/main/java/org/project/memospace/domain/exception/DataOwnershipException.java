package org.project.memospace.domain.exception;

import java.util.UUID;

public class DataOwnershipException extends RuntimeException {
    public DataOwnershipException(UUID resourceId, UUID expectedOwnerId, UUID actualOwnerId) {
        super(String.format(
            "Access denied: Resource %s is owned by user %s, not %s",
            resourceId, actualOwnerId, expectedOwnerId
        ));
    }

    public DataOwnershipException(String message) {
        super(message);
    }
}
