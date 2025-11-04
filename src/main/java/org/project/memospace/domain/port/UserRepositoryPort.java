package org.project.memospace.domain.port;

import org.project.memospace.domain.model.User;
import org.project.memospace.domain.model.Username;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for User aggregate.
 */
public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(Username username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(Username username);

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
