package org.project.memospace.adapter.web.dto.importexport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for import warning.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportWarningDto {
    private String code;
    private String detail;
}
