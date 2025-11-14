package org.memospace.service.handler.export;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.export.ExportAllQuery;
import org.memospace.model.exportimport.ExportModel;
import org.memospace.port.*;
import org.memospace.service.ExportService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExportAllQueryHandler implements QueryHandler<ExportAllQuery, ExportModel> {

    private final DeckRepositoryPort deckRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;
    private final MediaRepositoryPort mediaRepository;

    @Override
    @Transactional(readOnly = true)
    public ExportModel handle(ExportAllQuery query) {
        ExportService exportService = new ExportService(
                deckRepository,
                noteTypeRepository,
                noteRepository,
                cardRepository,
                mediaRepository
        );
        return exportService.exportAll(query.isIncludeMediaManifest());
    }
}
