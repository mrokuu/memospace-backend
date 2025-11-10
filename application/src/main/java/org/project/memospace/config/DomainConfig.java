package org.project.memospace.application.service.config;

import org.project.memospace.domain.service.FilteredDeckService;
import org.project.memospace.domain.service.QueryLanguageService;
import org.project.memospace.domain.service.SchedulerService;
import org.project.memospace.domain.service.stats.ForecastService;
import org.project.memospace.domain.service.stats.StatsService;
import org.project.memospace.domain.service.stats.StreakService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DomainConfig {

    @Bean
    public SchedulerService schedulerService() {
        return new SchedulerService();
    }

    @Bean
    public QueryLanguageService queryLanguageService() {
        return new QueryLanguageService();
    }

    @Bean
    public FilteredDeckService filteredDeckService(QueryLanguageService queryLanguageService) {
        return new FilteredDeckService(queryLanguageService);
    }

    @Bean
    public StatsService statsService() {
        return new StatsService();
    }

    @Bean
    public ForecastService forecastService() {
        return new ForecastService();
    }

    @Bean
    public StreakService streakService() {
        return new StreakService();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}