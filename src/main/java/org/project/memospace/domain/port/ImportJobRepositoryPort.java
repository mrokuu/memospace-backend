package org.project.memospace.domain.port;

import org.project.memospace.domain.model.importer.ImportJob;

import java.util.Optional;
import java.util.UUID;

public interface ImportJobRepositoryPort {
    ImportJob save(ImportJob job);

    Optional<ImportJob> findByRequestId(String requestId);

    Optional<ImportJob> findById(UUID id);
}
