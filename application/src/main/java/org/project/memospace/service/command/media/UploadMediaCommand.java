package org.project.memospace.application.service.command.media;

import org.project.memospace.application.service.Command;

import java.io.InputStream;

public record UploadMediaCommand(InputStream data, String originalFilename, String mimeType) implements Command {
}
