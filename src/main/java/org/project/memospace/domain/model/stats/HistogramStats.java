package org.project.memospace.domain.model.stats;

import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Histogram statistics including interval and ease distributions, answer buttons, and forecast.
 */
@Builder(toBuilder = true)
public record HistogramStats(IntervalHistogram intervals, EaseHistogram ease, AnswerButtons answerButtons,
                             List<DuePoint> dueForecast) {
    public HistogramStats(IntervalHistogram intervals, EaseHistogram ease,
                          AnswerButtons answerButtons, List<DuePoint> dueForecast) {
        this.intervals = Objects.requireNonNull(intervals);
        this.ease = Objects.requireNonNull(ease);
        this.answerButtons = Objects.requireNonNull(answerButtons);
        this.dueForecast = Objects.requireNonNull(dueForecast);
    }

    @Builder(toBuilder = true)
        public record IntervalHistogram(List<Bin> bins, int total) {
            public IntervalHistogram(List<Bin> bins, int total) {
                this.bins = Objects.requireNonNull(bins);
                this.total = total;
            }
        }

    @Builder(toBuilder = true)
        public record EaseHistogram(List<Bin> bins, int total) {
            public EaseHistogram(List<Bin> bins, int total) {
                this.bins = Objects.requireNonNull(bins);
                this.total = total;
            }
        }

    @Builder(toBuilder = true)
        public record Bin(String label, Double min, Double max, int count) {
            public Bin(String label, Double min, Double max, int count) {
                this.label = Objects.requireNonNull(label);
                this.min = min;
                this.max = max;
                this.count = count;
            }
        }

    @Builder(toBuilder = true)
        public record AnswerButtons(int again, int hard, int good, int easy) {
    }
}
