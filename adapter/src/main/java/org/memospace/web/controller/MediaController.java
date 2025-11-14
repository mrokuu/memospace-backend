package org.memospace.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.memospace.web.dto.MediaDiagnosticsResponse;
import org.memospace.web.dto.UploadMediaResponse;
import org.memospace.web.mapper.MediaWebMapper;
import org.memospace.service.CommandBus;
import org.memospace.service.QueryBus;
import org.memospace.service.command.media.UploadMediaCommand;
import org.memospace.service.handler.media.DiagnoseMediaQueryHandler;
import org.memospace.service.handler.media.StreamMediaQueryHandler;
import org.memospace.service.handler.media.UploadMediaCommandHandler;
import org.memospace.service.query.media.DiagnoseMediaQuery;
import org.memospace.service.query.media.StreamMediaQuery;
import org.memospace.exception.InvalidMediaException;
import org.memospace.model.MediaAsset;
import org.memospace.model.MediaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/media")
@Tag(name = "Media", description = "Media asset upload, download, and diagnostics")
@RequiredArgsConstructor
public class MediaController {

    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final MediaWebMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload media file", description = "Upload an image or audio file with content-based deduplication")
    @ApiResponse(responseCode = "201", description = "Media uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file or empty content")
    @ApiResponse(responseCode = "413", description = "File too large")
    @ApiResponse(responseCode = "415", description = "Unsupported media type")
    @ApiResponse(responseCode = "422", description = "Invalid filename")
    public ResponseEntity<UploadMediaResponse> uploadMedia(
            @Parameter(description = "Media file to upload")
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new InvalidMediaException("File is empty");
        }

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            if (contentType == null) {
                throw new InvalidMediaException("Content type is required");
            }

            UploadMediaCommandHandler.UploadResult result = commandBus.send(
                    new UploadMediaCommand(inputStream, originalFilename, contentType)
            );

            UploadMediaResponse response = mapper.toUploadResponse(result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            throw new InvalidMediaException("Failed to read uploaded file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download media file", description = "Stream a media file by its ID with caching headers")
    @ApiResponse(responseCode = "200", description = "Media file streamed successfully")
    @ApiResponse(responseCode = "404", description = "Media not found")
    public ResponseEntity<InputStreamResource> downloadMedia(
            @Parameter(description = "Media ID (64-character SHA-256 hex)")
            @PathVariable String id) {

        MediaId mediaId = MediaId.of(id);
        StreamMediaQueryHandler.StreamResult result = queryBus.send(new StreamMediaQuery(mediaId));

        MediaAsset asset = result.getAsset();
        InputStream stream = result.getStream();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(asset.getMimeType()));
        headers.setContentLength(asset.getSizeBytes());
        headers.setCacheControl("public, max-age=31536000, immutable");
        headers.setETag("\"" + asset.getId().value() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/diagnostics")
    @Operation(summary = "Run media diagnostics",
            description = "Check for missing files, orphans, dangling references, and unused assets")
    @ApiResponse(responseCode = "200", description = "Diagnostics completed successfully")
    public ResponseEntity<MediaDiagnosticsResponse> diagnostics() {
        DiagnoseMediaQueryHandler.DiagnosticResult result = queryBus.send(new DiagnoseMediaQuery());
        MediaDiagnosticsResponse response = mapper.toDiagnosticsResponse(result);
        return ResponseEntity.ok(response);
    }
}
