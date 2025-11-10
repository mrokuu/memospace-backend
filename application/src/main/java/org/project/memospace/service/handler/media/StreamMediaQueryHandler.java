package org.project.memospace.application.service.handler.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.media.StreamMediaQuery;
import org.project.memospace.domain.exception.MediaNotFoundException;
import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;
import org.project.memospace.domain.port.MediaRepositoryPort;
import org.project.memospace.domain.port.MediaStoragePort;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class StreamMediaQueryHandler implements QueryHandler<StreamMediaQuery, StreamMediaQueryHandler.StreamResult> {

    private final MediaStoragePort storagePort;
    private final MediaRepositoryPort repositoryPort;

    @Override
    public StreamResult handle(StreamMediaQuery query) {
        MediaId id = query.getMediaId();

        // Check if media exists in storage
        if (!storagePort.exists(id)) {
            throw new MediaNotFoundException("Media not found: " + id);
        }

        // Get metadata
        MediaAsset asset = repositoryPort.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media metadata not found: " + id));

        // Get stream
        InputStream stream = storagePort.get(id);

        return new StreamResult(stream, asset);
    }

    @Getter
    public static class StreamResult {
        private final InputStream stream;
        private final MediaAsset asset;

        public StreamResult(InputStream stream, MediaAsset asset) {
            this.stream = stream;
            this.asset = asset;
        }
    }
}
