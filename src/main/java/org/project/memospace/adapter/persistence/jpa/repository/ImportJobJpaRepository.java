package org.project.memospace.adapter.persistence.jpa.repository;

import org.project.memospace.adapter.persistence.jpa.entity.ImportJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImportJobJpaRepository extends JpaRepository<ImportJobEntity, UUID> {

    Optional<ImportJobEntity> findByRequestId(String requestId);
}
