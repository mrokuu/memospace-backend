package org.memospace.persistence.jpa.mapper;

import org.memospace.persistence.jpa.entity.FilteredDeckMembershipEntity;
import org.memospace.model.FilteredDeckMembership;
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
