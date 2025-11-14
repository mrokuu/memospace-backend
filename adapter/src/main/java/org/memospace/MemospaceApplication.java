package org.memospace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.project.memospace")
@EnableJpaRepositories(basePackages = "org.project.memospace.adapter.persistence.jpa.repository")
@EntityScan(basePackages = "org.project.memospace.adapter.persistence.jpa.entity")
public class MemospaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemospaceApplication.class, args);
    }

}
