package org.project.memospace.adapter.persistence;

import org.project.memospace.adapter.persistence.jpa.entity.MediaAssetEntity;
import org.project.memospace.adapter.persistence.jpa.mapper.MediaAssetEntityMapper;
import org.project.memospace.adapter.persistence.jpa.repository.MediaAssetJpaRepository;
import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;
import org.project.memospace.domain.port.MediaRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MediaRepositoryAdapter implements MediaRepositoryPort {

    private final MediaAssetJpaRepository jpaRepository;
    private final MediaAssetEntityMapper mapper;

    public MediaRepositoryAdapter(MediaAssetJpaRepository jpaRepository, MediaAssetEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<MediaAsset> findById(MediaId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public MediaAsset upsert(MediaAsset asset) {
        MediaAssetEntity entity = mapper.toEntity(asset);
        MediaAssetEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void incrementUsage(MediaId id, int delta) {
        jpaRepository.incrementUsageCount(id.value(), delta);
    }

    @Override
    public List<MediaAsset> findUnreferenced(Set<MediaId> referencedIds) {
        if (referencedIds.isEmpty()) {
            // If no references, all assets are unreferenced
            return findAll();
        }

        Set<String> referencedIdStrings = referencedIds.stream()
                .map(MediaId::value)
                .collect(Collectors.toSet());

        return jpaRepository.findUnreferenced(referencedIdStrings).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaAsset> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
