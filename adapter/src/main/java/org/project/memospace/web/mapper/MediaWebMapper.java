package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.MediaDiagnosticsResponse;
import org.project.memospace.adapter.web.dto.UploadMediaResponse;
import org.project.memospace.application.service.handler.media.DiagnoseMediaQueryHandler;
import org.project.memospace.application.service.handler.media.UploadMediaCommandHandler;
import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MediaWebMapper {

    public UploadMediaResponse toUploadResponse(UploadMediaCommandHandler.UploadResult result) {
        MediaAsset asset = result.getAsset();
        String url = "/api/v1/media/" + asset.getId().value();

        return new UploadMediaResponse(
                asset.getId().value(),
                url,
                asset.getMimeType(),
                asset.getSizeBytes(),
                asset.getOriginalFilename(),
                result.isDeduplicated()
        );
    }

    public MediaDiagnosticsResponse toDiagnosticsResponse(DiagnoseMediaQueryHandler.DiagnosticResult result) {
        MediaDiagnosticsResponse.DiagnosticSummaryDto summary = new MediaDiagnosticsResponse.DiagnosticSummaryDto(
                result.getSummary().getMissingOnDisk(),
                result.getSummary().getOrphansOnDisk(),
                result.getSummary().getDanglingReferences(),
                result.getSummary().getUnusedAssets()
        );

        return new MediaDiagnosticsResponse(
                summary,
                result.getMissingOnDisk().stream()
                        .map(MediaId::value)
                        .collect(Collectors.toList()),
                result.getOrphansOnDisk().stream()
                        .map(o -> new MediaDiagnosticsResponse.OrphanFileDto(o.getPath(), o.getSizeBytes()))
                        .collect(Collectors.toList()),
                result.getDanglingReferences().stream()
                        .map(d -> new MediaDiagnosticsResponse.DanglingReferenceDto(d.getNoteId(), d.getReference()))
                        .collect(Collectors.toList()),
                result.getUnusedAssets().stream()
                        .map(MediaId::value)
                        .collect(Collectors.toList())
        );
    }
}
