package org.project.deckservice.adapter.persistance.jpa;

import org.project.deckservice.domain.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
}
