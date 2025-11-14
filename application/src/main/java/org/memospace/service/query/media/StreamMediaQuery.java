package org.memospace.service.query.media;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.service.handler.media.StreamMediaQueryHandler;
import org.memospace.model.MediaId;

@Value
public class StreamMediaQuery implements Query<StreamMediaQueryHandler.StreamResult> {
    MediaId mediaId;
}
