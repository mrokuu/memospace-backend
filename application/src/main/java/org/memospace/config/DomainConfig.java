package org.memospace.config;

import org.memospace.service.FilteredDeckService;
import org.memospace.service.QueryLanguageService;
import org.memospace.service.SchedulerService;
import org.memospace.service.stats.ForecastService;
import org.memospace.service.stats.StatsService;
import org.memospace.service.stats.StreakService;
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