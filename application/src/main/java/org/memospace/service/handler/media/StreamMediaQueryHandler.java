package org.memospace.service.handler.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.media.StreamMediaQuery;
import org.memospace.exception.MediaNotFoundException;
import org.memospace.model.MediaAsset;
import org.memospace.model.MediaId;
import org.memospace.port.MediaRepositoryPort;
import org.memospace.port.MediaStoragePort;
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
