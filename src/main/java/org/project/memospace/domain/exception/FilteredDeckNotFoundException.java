package org.project.memospace.domain.exception;

import java.util.UUID;

public class FilteredDeckNotFoundException extends RuntimeException {
    public FilteredDeckNotFoundException(UUID filteredDeckId) {
        super("Filtered deck not found with id: " + filteredDeckId);
    }
}
