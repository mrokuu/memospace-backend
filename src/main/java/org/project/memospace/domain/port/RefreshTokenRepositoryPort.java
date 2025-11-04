package org.project.memospace.domain.port;

import org.project.memospace.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for RefreshToken aggregate.
 */
public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findById(UUID jti);

    List<RefreshToken> findByUserId(UUID userId);

    void deleteById(UUID jti);

    void deleteByUserId(UUID userId);

    void deleteExpired();
}
