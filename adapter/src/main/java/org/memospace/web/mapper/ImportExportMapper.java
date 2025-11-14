package org.memospace.web.mapper;

import org.memospace.model.exportimport.ImportSummary;
import org.memospace.web.dto.importexport.ImportResponseDto;
import org.memospace.web.dto.importexport.ImportSummaryDto;
import org.memospace.web.dto.importexport.ImportWarningDto;
import org.memospace.model.exportimport.ImportResult;
import org.memospace.model.exportimport.ImportWarning;
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

    private ImportSummaryDto toSummaryDto(ImportSummary summary) {
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
