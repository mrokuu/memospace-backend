package org.project.memospace.application.service.config;

import org.project.memospace.adapter.storage.FilesystemMediaStorageAdapter;
import org.project.memospace.adapter.storage.MimeTypeValidator;
import org.project.memospace.domain.port.MediaStoragePort;
import org.project.memospace.domain.service.FilenameSanitizer;
import org.project.memospace.domain.service.MediaReferenceExtractor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.List;

@Configuration
public class MediaConfig {

    @Bean
    public FilenameSanitizer filenameSanitizer() {
        return new FilenameSanitizer();
    }

    @Bean
    public MediaReferenceExtractor mediaReferenceExtractor() {
        return new MediaReferenceExtractor();
    }

    @Bean
    public MediaStoragePort mediaStoragePort(MediaProperties mediaProperties, MimeTypeValidator mimeTypeValidator) {
        Path rootPath = Paths.get(mediaProperties.getRoot());
        long maxSizeBytes = mediaProperties.getMaxSizeMb() * 1024L * 1024L;
        return new FilesystemMediaStorageAdapter(rootPath, mimeTypeValidator, maxSizeBytes);
    }

    @Bean
    public MimeTypeValidator mimeTypeValidator(MediaProperties mediaProperties) {
        return new MimeTypeValidator(
                mediaProperties.getAllow().getImages(),
                mediaProperties.getAllow().getAudio()
        );
    }

    @Bean
    @ConfigurationProperties(prefix = "media")
    public MediaProperties mediaProperties() {
        return new MediaProperties();
    }

    @lombok.Data
    public static class MediaProperties {
        private String root = "./media";
        private int maxSizeMb = 25;
        private AllowedTypes allow = new AllowedTypes();

        @lombok.Data
        public static class AllowedTypes {
            private List<String> images = List.of(
                    "image/png",
                    "image/jpeg",
                    "image/gif",
                    "image/webp",
                    "image/svg+xml"
            );
            private List<String> audio = List.of(
                    "audio/mpeg",
                    "audio/mp4",
                    "audio/ogg",
                    "audio/wav",
                    "audio/x-wav",
                    "audio/webm"
            );
        }
    }
}
