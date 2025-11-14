package org.memospace.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(UUID id) {
        super("Note not found with id: " + id);
    }
}