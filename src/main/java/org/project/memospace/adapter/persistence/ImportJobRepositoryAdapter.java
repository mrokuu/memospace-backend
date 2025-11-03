package org.project.memospace.adapter.persistence;

import org.project.memospace.adapter.persistence.jpa.mapper.ImportJobJpaMapper;
import org.project.memospace.adapter.persistence.jpa.repository.ImportJobJpaRepository;
import org.project.memospace.domain.model.importer.ImportJob;
import org.project.memospace.domain.port.ImportJobRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ImportJobRepositoryAdapter implements ImportJobRepositoryPort {

    private final ImportJobJpaRepository jpaRepository;
    private final ImportJobJpaMapper mapper;

    public ImportJobRepositoryAdapter(ImportJobJpaRepository jpaRepository, ImportJobJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ImportJob save(ImportJob job) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(job)));
    }

    @Override
    public Optional<ImportJob> findByRequestId(String requestId) {
        return jpaRepository.findByRequestId(requestId).map(mapper::toDomain);
    }

    @Override
    public Optional<ImportJob> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
