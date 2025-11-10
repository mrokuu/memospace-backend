package org.project.memospace.domain.service.stats;

import org.project.memospace.domain.model.stats.Streak;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain service for computing review streaks.
 */
public class StreakService {

    /**
     * Compute current and longest streaks from review counts per day.
     * A streak is a consecutive sequence of days with at least one review.
     *
     * @param reviewsPerDay Map of dates to review counts (only dates with reviews should be included)
     * @return Streak information
     */
    public Streak computeStreaks(Map<LocalDate, Integer> reviewsPerDay) {
        if (reviewsPerDay.isEmpty()) {
            return new Streak(0, 0);
        }

        // Filter to only days with reviews
        Set<LocalDate> datesWithReviews = reviewsPerDay.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (datesWithReviews.isEmpty()) {
            return new Streak(0, 0);
        }

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(datesWithReviews);
        Collections.sort(sortedDates);

        // Find all streaks
        List<Integer> streaks = new ArrayList<>();
        int currentStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate prev = sortedDates.get(i - 1);
            LocalDate curr = sortedDates.get(i);

            if (prev.plusDays(1).equals(curr)) {
                // Consecutive day
                currentStreak++;
            } else {
                // Gap found, save current streak and reset
                streaks.add(currentStreak);
                currentStreak = 1;
            }
        }
        streaks.add(currentStreak); // Don't forget the last streak

        int longest = streaks.stream().max(Integer::compare).orElse(0);

        // Current streak is the last one in the list (most recent)
        // But only if it extends to "today" or very recent
        // For simplicity, we'll use the last streak as current
        int current = streaks.get(streaks.size() - 1);

        return new Streak(current, longest);
    }

    /**
     * Compute streaks with explicit "today" for more accurate current streak.
     * Current streak only counts if it includes today.
     */
    public Streak computeStreaksWithToday(Map<LocalDate, Integer> reviewsPerDay, LocalDate today) {
        if (reviewsPerDay.isEmpty()) {
            return new Streak(0, 0);
        }

        // Filter to only days with reviews
        Set<LocalDate> datesWithReviews = reviewsPerDay.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (datesWithReviews.isEmpty()) {
            return new Streak(0, 0);
        }

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(datesWithReviews);
        Collections.sort(sortedDates);

        // Find all streaks
        List<StreakSegment> streaks = new ArrayList<>();
        LocalDate streakStart = sortedDates.get(0);
        LocalDate streakEnd = sortedDates.get(0);

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate prev = sortedDates.get(i - 1);
            LocalDate curr = sortedDates.get(i);

            if (prev.plusDays(1).equals(curr)) {
                // Consecutive day
                streakEnd = curr;
            } else {
                // Gap found, save current streak and reset
                streaks.add(new StreakSegment(streakStart, streakEnd));
                streakStart = curr;
                streakEnd = curr;
            }
        }
        streaks.add(new StreakSegment(streakStart, streakEnd)); // Don't forget the last streak

        int longest = streaks.stream()
                .mapToInt(StreakSegment::length)
                .max()
                .orElse(0);

        // Current streak: check if the last streak includes today (or yesterday to be lenient)
        StreakSegment lastStreak = streaks.get(streaks.size() - 1);
        int current = 0;
        if (lastStreak.end.equals(today) || lastStreak.end.equals(today.minusDays(1))) {
            current = lastStreak.length();
        }

        return new Streak(current, longest);
    }

    private static class StreakSegment {
        final LocalDate start;
        final LocalDate end;

        StreakSegment(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        int length() {
            return (int) (end.toEpochDay() - start.toEpochDay() + 1);
        }
    }
}
