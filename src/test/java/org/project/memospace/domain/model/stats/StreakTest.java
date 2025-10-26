package org.project.memospace.domain.model.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreakTest {

    @Test
    void shouldCreateStreakWithAllFields() {
        // Given
        int current = 5;
        int longest = 10;

        // When
        Streak streak = new Streak(current, longest);

        // Then
        assertEquals(current, streak.current());
        assertEquals(longest, streak.longest());
    }

    @Test
    void shouldCreateStreakWithZeroValues() {
        // When
        Streak streak = new Streak(0, 0);

        // Then
        assertEquals(0, streak.current());
        assertEquals(0, streak.longest());
    }

    @Test
    void shouldCreateStreakWhenCurrentEqualsLongest() {
        // When
        Streak streak = new Streak(7, 7);

        // Then
        assertEquals(7, streak.current());
        assertEquals(7, streak.longest());
    }

    @Test
    void shouldCreateStreakWhenCurrentIsLessThanLongest() {
        // When
        Streak streak = new Streak(3, 15);

        // Then
        assertEquals(3, streak.current());
        assertEquals(15, streak.longest());
    }

    @Test
    void shouldSupportBuilderPattern() {
        // When
        Streak streak = Streak.builder()
                .current(8)
                .longest(20)
                .build();

        // Then
        assertEquals(8, streak.current());
        assertEquals(20, streak.longest());
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        Streak original = Streak.builder()
                .current(5)
                .longest(10)
                .build();

        // When
        Streak modified = original.toBuilder()
                .current(6)
                .build();

        // Then
        assertEquals(5, original.current());
        assertEquals(6, modified.current());
        assertEquals(10, modified.longest());
    }

    @Test
    void shouldHandleLargeValues() {
        // When
        Streak streak = new Streak(365, 1000);

        // Then
        assertEquals(365, streak.current());
        assertEquals(1000, streak.longest());
    }
}
