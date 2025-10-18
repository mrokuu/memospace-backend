package org.project.memospace.application.service.query.media;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.application.service.handler.media.DiagnoseMediaQueryHandler;

@Value
public class DiagnoseMediaQuery implements Query<DiagnoseMediaQueryHandler.DiagnosticResult> {
    // No parameters needed - diagnoses all media
}
