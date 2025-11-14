package org.memospace.web.controller;

import org.memospace.web.dto.importexport.ImportResponseDto;
import org.memospace.web.mapper.ImportExportMapper;
import org.memospace.service.CommandBus;
import org.memospace.service.command.importexport.ImportDataCommand;
import org.memospace.model.exportimport.ExportModel;
import org.memospace.model.exportimport.ImportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for import operations.
 */
@RestController
@RequestMapping("/api/v1/import")
public class ImportController {
    private final CommandBus commandBus;
    private final ImportExportMapper mapper;

    public ImportController(CommandBus commandBus, ImportExportMapper mapper) {
        this.commandBus = commandBus;
        this.mapper = mapper;
    }

    /**
     * Imports collection data from JSON.
     *
     * POST /api/v1/import/json
     * Header: X-NoteType-Mapping (optional JSON object)
     * Body: ExportModel JSON
     *
     * @param exportModel the import data
     * @param noteTypeMappingHeader optional note type mapping header
     * @return import result with summary and warnings
     */
    @PostMapping("/json")
    public ResponseEntity<ImportResponseDto> importJson(
            @RequestBody ExportModel exportModel,
            @RequestHeader(value = "X-NoteType-Mapping", required = false) Map<String, String> noteTypeMappingHeader) {

        ImportResult result = commandBus.send(new ImportDataCommand(exportModel, noteTypeMappingHeader));
        ImportResponseDto response = mapper.toImportResponseDto(result);

        return ResponseEntity.ok(response);
    }
}
