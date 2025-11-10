package org.project.memospace.application.service.query.export;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.exportimport.ExportModel;

@Value
public class ExportDeckQuery implements Query<ExportModel> {
    Long deckId;
    boolean includeMediaManifest;
}
