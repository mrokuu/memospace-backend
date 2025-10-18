package org.project.memospace.adapter.persistence.jpa.repository;

import org.project.memospace.adapter.persistence.jpa.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
}