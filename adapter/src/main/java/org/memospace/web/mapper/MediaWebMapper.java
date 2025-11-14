package org.memospace.web.mapper;

import org.memospace.web.dto.MediaDiagnosticsResponse;
import org.memospace.web.dto.UploadMediaResponse;
import org.memospace.service.handler.media.DiagnoseMediaQueryHandler;
import org.memospace.service.handler.media.UploadMediaCommandHandler;
import org.memospace.model.MediaAsset;
import org.memospace.model.MediaId;
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
