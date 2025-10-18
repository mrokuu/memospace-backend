package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.importexport.ImportResponseDto;
import org.project.memospace.adapter.web.dto.importexport.ImportSummaryDto;
import org.project.memospace.adapter.web.dto.importexport.ImportWarningDto;
import org.project.memospace.domain.model.exportimport.ImportResult;
import org.project.memospace.domain.model.exportimport.ImportWarning;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for import/export DTOs.
 */
@Component
public class ImportExportMapper {

    public ImportResponseDto toImportResponseDto(ImportResult result) {
        ImportResponseDto dto = new ImportResponseDto();
        dto.setSummary(toSummaryDto(result.summary()));
        dto.setIdMapping(result.idMapping());
        dto.setWarnings(result.warnings().stream()
                .map(this::toWarningDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private ImportSummaryDto toSummaryDto(org.project.memospace.domain.model.exportimport.ImportSummary summary) {
        ImportSummaryDto dto = new ImportSummaryDto();

        ImportSummaryDto.CreatedDto created = new ImportSummaryDto.CreatedDto();
        created.setDecks(summary.decksCreated());
        created.setNoteTypes(summary.noteTypesCreated());
        created.setNotes(summary.notesCreated());
        created.setCards(summary.cardsCreated());
        created.setMediaLinked(summary.mediaLinked());
        dto.setCreated(created);

        ImportSummaryDto.UpdatedDto updated = new ImportSummaryDto.UpdatedDto();
        updated.setNotes(summary.notesUpdated());
        updated.setCards(summary.cardsUpdated());
        dto.setUpdated(updated);

        ImportSummaryDto.SkippedDto skipped = new ImportSummaryDto.SkippedDto();
        skipped.setDuplicateNotes(summary.notesSkipped());
        dto.setSkipped(skipped);

        return dto;
    }

    private ImportWarningDto toWarningDto(ImportWarning warning) {
        ImportWarningDto dto = new ImportWarningDto();
        dto.setCode(warning.code().name());
        dto.setDetail(warning.detail());
        return dto;
    }
}
