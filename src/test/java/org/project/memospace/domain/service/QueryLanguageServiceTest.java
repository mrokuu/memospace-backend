package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.exception.InvalidQueryException;
import org.project.memospace.domain.model.browser.query.QuerySpec;

import static org.junit.jupiter.api.Assertions.*;

class QueryLanguageServiceTest {

    private QueryLanguageService queryLanguageService;

    @BeforeEach
    void setUp() {
        queryLanguageService = new QueryLanguageService();
    }

    @Test
    void shouldParseSimpleQuery() {
        // Given
        String query = "tag:spanish";

        // When
        QuerySpec result = queryLanguageService.parse(query);

        // Then
        assertNotNull(result);
        assertEquals("tag:spanish", result.rawQuery());
    }

    @Test
    void shouldParseComplexQuery() {
        // Given
        String query = "deck:Spanish tag:verb is:due";

        // When
        QuerySpec result = queryLanguageService.parse(query);

        // Then
        assertNotNull(result);
        assertEquals("deck:Spanish tag:verb is:due", result.rawQuery());
    }

    @Test
    void shouldThrowExceptionForNullQuery() {
        // When / Then
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                () -> queryLanguageService.parse(null)
        );
        assertTrue(exception.getMessage().startsWith("Query cannot be null or empty"));
        assertEquals(0, exception.getPosition());
    }

    @Test
    void shouldThrowExceptionForEmptyQuery() {
        // When / Then
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                () -> queryLanguageService.parse("")
        );
        assertTrue(exception.getMessage().startsWith("Query cannot be null or empty"));
    }

    @Test
    void shouldThrowExceptionForWhitespaceQuery() {
        // When / Then
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                () -> queryLanguageService.parse("   ")
        );
        assertTrue(exception.getMessage().startsWith("Query cannot be null or empty"));
    }

    @Test
    void shouldParseQueryWithWhitespace() {
        // Given
        String query = "  tag:spanish  ";

        // When
        QuerySpec result = queryLanguageService.parse(query);

        // Then
        assertNotNull(result);
        // Query is trimmed and wrapped
        assertEquals("  tag:spanish  ", result.rawQuery());
    }
}
