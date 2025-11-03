package org.project.memospace.application.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TxtImportConfig {

    @Bean
    @ConfigurationProperties(prefix = "import.txt")
    public TxtImportProperties txtImportProperties() {
        return new TxtImportProperties();
    }

    @lombok.Data
    public static class TxtImportProperties {
        private int maxLineLength = 10_000;
        private int maxLines = 100_000;
        private int batchSize = 500;
        private int previewSampleSize = 10;
        private int maxFileSizeMb = 20;
        private boolean strictDefault = false;
    }
}
