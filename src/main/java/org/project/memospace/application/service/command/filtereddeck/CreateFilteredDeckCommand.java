package org.project.memospace.application.service.command.filtereddeck;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.FilteredMode;
import org.project.memospace.domain.model.ReturnPolicy;

import java.time.Instant;
import java.util.UUID;

public record CreateFilteredDeckCommand(String name, UUID ownerDeckId, String query, int limit, FilteredMode mode,
                                        ReturnPolicy returnPolicy, Instant expiresAt) implements Command {
}
