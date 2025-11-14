package org.memospace.model.stats;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Overview statistics including daily series and totals.
 */
@Builder(toBuilder = true)
public record OverviewStats(List<ReviewsPerDay> reviewsPerDay, List<RetentionPerDay> retentionPerDay,
                            List<AnswerButtonsPerDay> answerButtonsPerDay, List<TimeSpentPerDay> timeSpentPerDay,
                            Totals totals) {
    public OverviewStats(List<ReviewsPerDay> reviewsPerDay,
                         List<RetentionPerDay> retentionPerDay,
                         List<AnswerButtonsPerDay> answerButtonsPerDay,
                         List<TimeSpentPerDay> timeSpentPerDay,
                         Totals totals) {
        this.reviewsPerDay = Objects.requireNonNull(reviewsPerDay);
        this.retentionPerDay = Objects.requireNonNull(retentionPerDay);
        this.answerButtonsPerDay = Objects.requireNonNull(answerButtonsPerDay);
        this.timeSpentPerDay = Objects.requireNonNull(timeSpentPerDay);
        this.totals = Objects.requireNonNull(totals);
    }

    @Builder(toBuilder = true)
        public record ReviewsPerDay(LocalDate date, int count) {
            public ReviewsPerDay(LocalDate date, int count) {
                this.date = Objects.requireNonNull(date);
                this.count = count;
            }
        }

    @Value
    public static class RetentionPerDay {
        LocalDate date;
        int passed;
        int total;
        double ratio;

        public RetentionPerDay(LocalDate date, int passed, int total) {
            this.date = Objects.requireNonNull(date);
            this.passed = passed;
            this.total = total;
            this.ratio = total > 0 ? (double) passed / total : 0.0;
        }

        public RetentionPerDay(LocalDate date, int passed, int total, double ratio) {
            this.date = Objects.requireNonNull(date);
            this.passed = passed;
            this.total = total;
            this.ratio = ratio;
        }
    }

    @Builder(toBuilder = true)
        public record AnswerButtonsPerDay(LocalDate date, int again, int hard, int good, int easy) {
            public AnswerButtonsPerDay(LocalDate date, int again, int hard, int good, int easy) {
                this.date = Objects.requireNonNull(date);
                this.again = again;
                this.hard = hard;
                this.good = good;
                this.easy = easy;
            }
        }

    @Builder(toBuilder = true)
        public record TimeSpentPerDay(LocalDate date, long milliseconds) {
            public TimeSpentPerDay(LocalDate date, long milliseconds) {
                this.date = Objects.requireNonNull(date);
                this.milliseconds = milliseconds;
            }
        }

    @Value
    public static class Totals {
        int reviews;
        int passed;
        double retentionRatio;
        long timeSpentMs;

        public Totals(int reviews, int passed, long timeSpentMs) {
            this.reviews = reviews;
            this.passed = passed;
            this.retentionRatio = reviews > 0 ? (double) passed / reviews : 0.0;
            this.timeSpentMs = timeSpentMs;
        }
    }
}
