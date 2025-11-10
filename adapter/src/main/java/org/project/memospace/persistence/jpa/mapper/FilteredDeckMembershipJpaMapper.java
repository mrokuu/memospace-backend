package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.FilteredDeckMembershipEntity;
import org.project.memospace.domain.model.FilteredDeckMembership;
import org.springframework.stereotype.Component;

@Component
public class FilteredDeckMembershipJpaMapper {

    public FilteredDeckMembershipEntity toEntity(FilteredDeckMembership membership) {
        return new FilteredDeckMembershipEntity(
                membership.filteredDeckId(),
                membership.cardId(),
                membership.position(),
                membership.addedAt()
        );
    }

    public FilteredDeckMembership toDomain(FilteredDeckMembershipEntity entity) {
        return new FilteredDeckMembership(
                entity.getFilteredDeckId(),
                entity.getCardId(),
                entity.getPosition(),
                entity.getAddedAt()
        );
    }
}
