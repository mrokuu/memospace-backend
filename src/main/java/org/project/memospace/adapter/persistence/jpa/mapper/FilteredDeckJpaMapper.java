package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.FilteredDeckEntity;
import org.project.memospace.domain.model.FilteredDeck;
import org.springframework.stereotype.Component;

@Component
public class FilteredDeckJpaMapper {

    public FilteredDeckEntity toEntity(FilteredDeck filteredDeck) {
        return new FilteredDeckEntity(
                filteredDeck.id(),
                filteredDeck.name(),
                filteredDeck.ownerDeckId(),
                filteredDeck.query(),
                filteredDeck.limit(),
                filteredDeck.mode(),
                filteredDeck.returnPolicy(),
                filteredDeck.createdAt(),
                filteredDeck.expiresAt(),
                filteredDeck.lastBuiltAt()
        );
    }

    public FilteredDeck toDomain(FilteredDeckEntity entity) {
        return new FilteredDeck(
                entity.getId(),
                entity.getName(),
                entity.getOwnerDeckId(),
                entity.getQuery(),
                entity.getLimit(),
                entity.getMode(),
                entity.getReturnPolicy(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getLastBuiltAt()
        );
    }
}
