package org.project.memospace.domain.port;

import org.project.memospace.domain.model.MediaAsset;
import org.project.memospace.domain.model.MediaId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Port for media asset metadata persistence.
 * Stores metadata in database (H2), not the actual file content.
 */
public interface MediaRepositoryPort {

    /**
     * Finds media asset by ID.
     *
     * @param id media identifier
     * @return optional containing media asset if found
     */
    Optional<MediaAsset> findById(MediaId id);

    /**
     * Saves or updates media asset metadata.
     *
     * @param asset media asset to upsert
     * @return saved media asset
     */
    MediaAsset upsert(MediaAsset asset);

    /**
     * Increments usage count for a media asset.
     *
     * @param id media identifier
     * @param delta amount to increment (can be negative to decrement)
     */
    void incrementUsage(MediaId id, int delta);

    /**
     * Finds media assets that are not referenced by any notes.
     * Used for diagnostics to identify unused assets.
     *
     * @param referencedIds set of media IDs that are currently referenced
     * @return list of unreferenced media assets
     */
    List<MediaAsset> findUnreferenced(Set<MediaId> referencedIds);

    /**
     * Lists all media assets in the repository.
     *
     * @return list of all media assets
     */
    List<MediaAsset> findAll();
}
