package org.project.memospace.application.service.query.media;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.application.service.handler.media.StreamMediaQueryHandler;
import org.project.memospace.domain.model.MediaId;

@Value
public class StreamMediaQuery implements Query<StreamMediaQueryHandler.StreamResult> {
    MediaId mediaId;
}
