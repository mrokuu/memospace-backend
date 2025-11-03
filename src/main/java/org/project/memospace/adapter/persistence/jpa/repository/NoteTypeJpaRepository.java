package org.project.memospace.adapter.persistence.jpa.repository;

import org.project.memospace.adapter.persistence.jpa.entity.NoteTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteTypeJpaRepository extends JpaRepository<NoteTypeEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    Optional<NoteTypeEntity> findByNameIgnoreCase(String name);
}
