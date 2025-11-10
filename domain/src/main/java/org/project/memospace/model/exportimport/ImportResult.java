package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain model representing the result of an import operation.
 * Pure domain model with no framework dependencies.
 *
 * @param idMapping oldId -> newId
 */
@Builder(toBuilder = true)
public record ImportResult(ImportSummary summary, Map<String, String> idMapping, List<ImportWarning> warnings) {
    public ImportResult(ImportSummary summary, Map<String, String> idMapping, List<ImportWarning> warnings) {
        if (summary == null) {
            throw new IllegalArgumentException("Summary cannot be null");
        }
        if (idMapping == null) {
            throw new IllegalArgumentException("IdMapping cannot be null");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("Warnings cannot be null");
        }

        this.summary = summary;
        this.idMapping = Collections.unmodifiableMap(new HashMap<>(idMapping));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    public static ImportResult empty() {
        return new ImportResult(ImportSummary.empty(), Collections.emptyMap(), Collections.emptyList());
    }
}
