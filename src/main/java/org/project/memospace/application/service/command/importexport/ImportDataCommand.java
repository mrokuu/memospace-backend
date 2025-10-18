package org.project.memospace.application.service.command.importexport;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.exportimport.ExportModel;

import java.util.Map;

public record ImportDataCommand(ExportModel exportModel, Map<String, String> noteTypeMapping) implements Command {
}
