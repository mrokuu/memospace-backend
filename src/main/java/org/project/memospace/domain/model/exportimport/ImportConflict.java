package org.project.memospace.domain.model.exportimport;

/**
 * Enumeration of possible import conflicts.
 */
public enum ImportConflict {
    DUPLICATE_NOTE,
    UNKNOWN_NOTETYPE,
    NOTETYPE_SHAPE_MISMATCH,
    DECK_NOT_FOUND,
    INVALID_MEDIA_REF,
    INVALID_TEMPLATE_REF,
    INVALID_FIELD_VALUES,
    UNSUPPORTED_VERSION
}
