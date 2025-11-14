package org.memospace.service.command.media;

import org.memospace.service.Command;

import java.io.InputStream;

public record UploadMediaCommand(InputStream data, String originalFilename, String mimeType) implements Command {
}
