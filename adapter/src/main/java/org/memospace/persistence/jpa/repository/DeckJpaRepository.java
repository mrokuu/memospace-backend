package org.memospace.persistence.jpa.repository;

import org.memospace.persistence.jpa.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
}