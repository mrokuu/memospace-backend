package org.memospace.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "media_assets", indexes = {
        @Index(name = "idx_media_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAssetEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "mime_type", length = 100, nullable = false)
    private String mimeType;

    @Column(name = "extension", length = 16, nullable = false)
    private String extension;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Builder.Default
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;
}
