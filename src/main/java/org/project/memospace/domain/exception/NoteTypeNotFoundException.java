package org.project.memospace.domain.exception;

import java.util.UUID;

public class NoteTypeNotFoundException extends RuntimeException {
    public NoteTypeNotFoundException(UUID id) {
        super("NoteType not found with id: " + id);
    }

    public NoteTypeNotFoundException(String name) {
        super("NoteType not found with name: " + name);
    }
}