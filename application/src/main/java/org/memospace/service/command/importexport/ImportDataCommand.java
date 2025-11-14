package org.memospace.service.command.importexport;

import org.memospace.service.Command;
import org.memospace.model.exportimport.ExportModel;

import java.util.Map;

public record ImportDataCommand(ExportModel exportModel, Map<String, String> noteTypeMapping) implements Command {
}
