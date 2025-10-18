package org.project.memospace.adapter.web.dto.importexport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for import response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponseDto {
    private ImportSummaryDto summary;
    private Map<String, String> idMapping;
    private List<ImportWarningDto> warnings;
}
