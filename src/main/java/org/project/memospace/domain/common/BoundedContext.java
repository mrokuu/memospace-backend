package org.project.memospace.domain.common;

/**
 * Enumeration of bounded contexts in the Memospace domain.
 * Used for documentation, event routing, and architectural governance.
 *
 * See docs/context-map.md for detailed context descriptions and relationships.
 */
public enum BoundedContext {
    /**
     * Authoring Context: Manages note types, notes, card templates, and card generation.
     */
    AUTHORING,

    /**
     * Learning/Scheduling Context: Manages card scheduling state and SM-2 algorithm.
     */
    LEARNING,

    /**
     * Collection Context: Organizes cards into deck hierarchies and manages deck options.
     */
    COLLECTION,

    /**
     * Review Context: Records immutable review history and answer quality.
     */
    REVIEW,

    /**
     * Media Context: Manages media assets with content-addressed storage.
     */
    MEDIA,

    /**
     * Search/Browser Context: Query language parsing and card/note filtering.
     */
    SEARCH,

    /**
     * Import/Export Context: Import/export in various formats with ACL.
     */
    IMPORT_EXPORT,

    /**
     * Analytics/Stats Context: Aggregate review statistics and forecasting (future).
     */
    ANALYTICS,

    /**
     * IAM Context: User authentication, authorization, and profile management (future).
     */
    IAM,

    /**
     * Sync Context: Multi-device synchronization (future).
     */
    SYNC
}
