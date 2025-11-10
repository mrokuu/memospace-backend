package org.project.memospace.adapter.web.controller;

import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.query.export.ExportAllQuery;
import org.project.memospace.application.service.query.export.ExportDeckQuery;
import org.project.memospace.domain.model.exportimport.ExportModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for export operations.
 */
@RestController
@RequestMapping("/api/v1/export")
public class ExportController {
    private final QueryBus queryBus;

    public ExportController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * Exports collection or deck data as JSON.
     *
     * GET /api/v1/export/json?deckId=123&includeMediaManifest=true
     *
     * @param deckId optional deck ID to export (if null, exports entire collection)
     * @param includeMediaManifest whether to include media manifest (default: true)
     * @return export model as JSON
     */
    @GetMapping("/json")
    public ResponseEntity<ExportModel> exportJson(
            @RequestParam(required = false) Long deckId,
            @RequestParam(defaultValue = "true") boolean includeMediaManifest) {

        ExportModel exportModel;
        if (deckId != null) {
            exportModel = queryBus.send(new ExportDeckQuery(deckId, includeMediaManifest));
        } else {
            exportModel = queryBus.send(new ExportAllQuery(includeMediaManifest));
        }

        return ResponseEntity.ok(exportModel);
    }
}
