package org.memospace.service.command.filtereddeck;

import org.memospace.service.Command;
import org.memospace.model.FilteredMode;
import org.memospace.model.ReturnPolicy;

import java.time.Instant;
import java.util.UUID;

public record CreateFilteredDeckCommand(String name, UUID ownerDeckId, String query, int limit, FilteredMode mode,
                                        ReturnPolicy returnPolicy, Instant expiresAt) implements Command {
}
