package org.memospace.service.query.export;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.exportimport.ExportModel;

@Value
public class ExportAllQuery implements Query<ExportModel> {
    boolean includeMediaManifest;
}
