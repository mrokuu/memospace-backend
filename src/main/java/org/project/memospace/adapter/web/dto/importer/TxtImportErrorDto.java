package org.project.memospace.adapter.web.dto.importer;

import lombok.Data;

@Data
public class TxtImportErrorDto {
    private int lineNumber;
    private String message;
}
