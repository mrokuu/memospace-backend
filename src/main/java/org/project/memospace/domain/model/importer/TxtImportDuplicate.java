package org.project.memospace.domain.model.importer;

public record TxtImportDuplicate(int lineNumber, String reason, String front, String back) {
}
