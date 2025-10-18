package org.project.memospace.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.stats.*;
import org.project.memospace.adapter.web.mapper.StatsWebMapper;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.query.stats.*;
import org.project.memospace.domain.model.stats.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

@RestController
@RequestMapping("/api/v1/stats")
@Tag(name = "Statistics", description = "Statistics and analytics operations")
@RequiredArgsConstructor
public class StatsController {

    private final QueryBus queryBus;
    private final StatsWebMapper mapper;
    private final Clock clock;

    @GetMapping("/overview")
    @Operation(summary = "Get statistics overview",
               description = "Retrieve overview statistics including daily reviews, retention, answer buttons, and time spent")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid window parameter")
    public ResponseEntity<StatsOverviewDto> getOverview(
            @Parameter(description = "Deck ID (omit for all decks)")
            @RequestParam(required = false) Long deckId,

            @Parameter(description = "Time window: 7d, 30d, 90d, 365d, all", example = "30d")
            @RequestParam(defaultValue = "30d") String window
    ) {
        // Use default cutoff for now
        Cutoff cutoff = Cutoff.defaultCutoff();

        // Parse window to get Window object for mapper
        Window windowObj = parseWindow(window, cutoff, clock);

        GetStatsOverviewQuery query = new GetStatsOverviewQuery(deckId, window);
        OverviewStats stats = queryBus.send(query);

        StatsOverviewDto dto = mapper.toOverviewDto(stats,
                deckId != null ? deckId.toString() : null,
                window, cutoff, windowObj);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/heatmap")
    @Operation(summary = "Get heatmap statistics",
               description = "Retrieve heatmap data showing reviews per day for a given year, including streaks")
    @ApiResponse(responseCode = "200", description = "Heatmap retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid year parameter")
    public ResponseEntity<StatsHeatmapDto> getHeatmap(
            @Parameter(description = "Year (e.g., 2025)", example = "2025")
            @RequestParam int year,

            @Parameter(description = "Deck ID (omit for all decks)")
            @RequestParam(required = false) Long deckId
    ) {
        Cutoff cutoff = Cutoff.defaultCutoff();

        GetStatsHeatmapQuery query = new GetStatsHeatmapQuery(deckId, year);
        HeatmapStats stats = queryBus.send(query);

        StatsHeatmapDto dto = mapper.toHeatmapDto(stats, cutoff);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/histograms")
    @Operation(summary = "Get histogram statistics",
               description = "Retrieve histograms for intervals, ease factors, answer buttons, and due forecast")
    @ApiResponse(responseCode = "200", description = "Histograms retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid window parameter")
    public ResponseEntity<StatsHistogramsDto> getHistograms(
            @Parameter(description = "Deck ID (omit for all decks)")
            @RequestParam(required = false) Long deckId,

            @Parameter(description = "Time window: 30d, 90d, 365d, all", example = "90d")
            @RequestParam(defaultValue = "90d") String window
    ) {
        GetStatsHistogramsQuery query = new GetStatsHistogramsQuery(deckId, window);
        HistogramStats stats = queryBus.send(query);

        StatsHistogramsDto dto = mapper.toHistogramsDto(stats,
                deckId != null ? deckId.toString() : null,
                window);

        return ResponseEntity.ok(dto);
    }

    private Window parseWindow(String windowStr, Cutoff cutoff, Clock clock) {
        java.time.LocalDate today = cutoff.today(clock);

        return switch (windowStr) {
            case "7d" -> Window.fromDays(7, cutoff, clock);
            case "30d" -> Window.fromDays(30, cutoff, clock);
            case "90d" -> Window.fromDays(90, cutoff, clock);
            case "365d" -> Window.fromDays(365, cutoff, clock);
            case "all" -> {
                java.time.LocalDate earliest = today.minusYears(10);
                yield Window.all(earliest, cutoff, clock);
            }
            default -> throw new IllegalArgumentException("Invalid window: " + windowStr);
        };
    }
}
