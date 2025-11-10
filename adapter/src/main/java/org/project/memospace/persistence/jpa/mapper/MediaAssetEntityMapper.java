package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.MediaAssetEntity;
import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;
import org.springframework.stereotype.Component;

@Component
public class MediaAssetEntityMapper {

    public MediaAsset toDomain(MediaAssetEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MediaAsset(
                MediaId.of(entity.getId()),
                entity.getOriginalFilename(),
                entity.getMimeType(),
                entity.getExtension(),
                entity.getSizeBytes(),
                entity.getCreatedAt(),
                entity.getUsageCount()
        );
    }

    public MediaAssetEntity toEntity(MediaAsset domain) {
        if (domain == null) {
            return null;
        }

        MediaAssetEntity entity = new MediaAssetEntity();
        entity.setId(domain.getId().value());
        entity.setOriginalFilename(domain.getOriginalFilename());
        entity.setMimeType(domain.getMimeType());
        entity.setExtension(domain.getExtension());
        entity.setSizeBytes(domain.getSizeBytes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUsageCount(domain.getUsageCount());

        return entity;
    }
}
