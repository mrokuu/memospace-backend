package org.project.memospace.domain.port;

import org.project.memospace.domain.model.Profile;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Profile aggregate.
 */
public interface ProfileRepositoryPort {
    Profile save(Profile profile);

    Optional<Profile> findById(UUID id);

    Optional<Profile> findByUserId(UUID userId);

    void deleteById(UUID id);

    void deleteByUserId(UUID userId);
}
