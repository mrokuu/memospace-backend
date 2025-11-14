package org.memospace.service.query.media;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.service.handler.media.DiagnoseMediaQueryHandler;

@Value
public class DiagnoseMediaQuery implements Query<DiagnoseMediaQueryHandler.DiagnosticResult> {
    // No parameters needed - diagnoses all media
}
