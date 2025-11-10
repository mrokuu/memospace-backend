package org.project.memospace.domain.port;

import java.io.InputStream;
import java.util.List;

/**
 * Port for ZIP archive operations.
 * Used for APKG import functionality.
 */
public interface ZipArchivePort {

    /**
     * Information about a ZIP entry.
     */
    class ZipEntryInfo {
        private final String name;
        private final long size;
        private final boolean isDirectory;

        public ZipEntryInfo(String name, long size, boolean isDirectory) {
            this.name = name;
            this.size = size;
            this.isDirectory = isDirectory;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public boolean isDirectory() {
            return isDirectory;
        }
    }

    /**
     * Lists all entries in a ZIP archive.
     *
     * @param zipPath path to ZIP file
     * @return list of ZIP entries
     */
    List<ZipEntryInfo> listEntries(String zipPath);

    /**
     * Opens a specific entry in a ZIP archive for reading.
     *
     * @param zipPath path to ZIP file
     * @param entryName name of entry to open
     * @return input stream for reading entry
     */
    InputStream openEntry(String zipPath, String entryName);
}
