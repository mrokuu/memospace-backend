package org.memospace.service.command.note;

import org.memospace.service.Command;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record UpdateNoteCommand(UUID noteId, Long deckId, Map<String, String> fieldValues,
                                Set<String> tags) implements Command {
}