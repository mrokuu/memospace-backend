package org.project.memospace.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.application.service.Query;
import org.project.memospace.application.service.QueryHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleQueryBusTest {

    private SimpleQueryBus queryBus;

    @BeforeEach
    void setUp() {
        queryBus = new SimpleQueryBus();
    }

    @Test
    void shouldRegisterAndExecuteQueryHandler() {
        // Given
        TestQuery query = new TestQuery("test");
        QueryHandler<TestQuery, String> handler = spy(new TestQueryHandler());
        queryBus.registerHandler(TestQuery.class, handler);

        // When
        String result = queryBus.send(query);

        // Then
        assertEquals("result: test", result);
        verify(handler).handle(query);
    }

    @Test
    void shouldThrowExceptionWhenHandlerNotRegistered() {
        // Given
        TestQuery query = new TestQuery("test");

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> queryBus.send(query));
        assertTrue(exception.getMessage().contains("No handler registered for query"));
        assertTrue(exception.getMessage().contains("TestQuery"));
    }

    @Test
    void shouldRegisterMultipleHandlers() {
        // Given
        TestQuery query1 = new TestQuery("test1");
        AnotherTestQuery query2 = new AnotherTestQuery("test2");

        QueryHandler<TestQuery, String> handler1 = new TestQueryHandler();
        QueryHandler<AnotherTestQuery, Integer> handler2 = new AnotherTestQueryHandler();

        queryBus.registerHandler(TestQuery.class, handler1);
        queryBus.registerHandler(AnotherTestQuery.class, handler2);

        // When
        String result1 = queryBus.send(query1);
        Integer result2 = queryBus.send(query2);

        // Then
        assertEquals("result: test1", result1);
        assertEquals(5, result2);
    }

    @Test
    void shouldOverrideHandlerWhenRegisteredTwice() {
        // Given
        TestQuery query = new TestQuery("test");
        QueryHandler<TestQuery, String> originalHandler = new TestQueryHandler();
        QueryHandler<TestQuery, String> newHandler = spy(new CustomTestQueryHandler());

        queryBus.registerHandler(TestQuery.class, originalHandler);
        queryBus.registerHandler(TestQuery.class, newHandler);

        // When
        String result = queryBus.send(query);

        // Then
        assertEquals("custom: test", result);
        verify(newHandler).handle(query);
    }

    @Test
    void shouldPropagateExceptionsFromHandler() {
        // Given
        TestQuery query = new TestQuery("fail");
        QueryHandler<TestQuery, String> handler = new FailingQueryHandler();
        queryBus.registerHandler(TestQuery.class, handler);

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queryBus.send(query));
        assertEquals("Query handler failed", exception.getMessage());
    }

    @Test
    void shouldHandleNullReturnValue() {
        // Given
        TestQuery query = new TestQuery("test");
        QueryHandler<TestQuery, String> handler = new NullReturningQueryHandler();
        queryBus.registerHandler(TestQuery.class, handler);

        // When
        String result = queryBus.send(query);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleDifferentReturnTypes() {
        // Given
        IntegerQuery query = new IntegerQuery();
        QueryHandler<IntegerQuery, Integer> handler = new IntegerQueryHandler();
        queryBus.registerHandler(IntegerQuery.class, handler);

        // When
        Integer result = queryBus.send(query);

        // Then
        assertEquals(42, result);
    }

    @Test
    void shouldHandleComplexReturnTypes() {
        // Given
        ComplexQuery query = new ComplexQuery("value");
        QueryHandler<ComplexQuery, ComplexResult> handler = new ComplexQueryHandler();
        queryBus.registerHandler(ComplexQuery.class, handler);

        // When
        ComplexResult result = queryBus.send(query);

        // Then
        assertNotNull(result);
        assertEquals("value", result.data());
        assertEquals(5, result.count());
    }

    @Test
    void shouldHandleEmptyQueries() {
        // Given
        EmptyQuery query = new EmptyQuery();
        QueryHandler<EmptyQuery, String> handler = new EmptyQueryHandler();
        queryBus.registerHandler(EmptyQuery.class, handler);

        // When
        String result = queryBus.send(query);

        // Then
        assertEquals("empty", result);
    }

    // Test queries and handlers

    record TestQuery(String value) implements Query<String> {
    }

    record AnotherTestQuery(String value) implements Query<Integer> {
    }

    record IntegerQuery() implements Query<Integer> {
    }

    record ComplexQuery(String value) implements Query<ComplexResult> {
    }

    record EmptyQuery() implements Query<String> {
    }

    record ComplexResult(String data, int count) {
    }

    static class TestQueryHandler implements QueryHandler<TestQuery, String> {
        @Override
        public String handle(TestQuery query) {
            return "result: " + query.value();
        }
    }

    static class CustomTestQueryHandler implements QueryHandler<TestQuery, String> {
        @Override
        public String handle(TestQuery query) {
            return "custom: " + query.value();
        }
    }

    static class AnotherTestQueryHandler implements QueryHandler<AnotherTestQuery, Integer> {
        @Override
        public Integer handle(AnotherTestQuery query) {
            return query.value().length();
        }
    }

    static class FailingQueryHandler implements QueryHandler<TestQuery, String> {
        @Override
        public String handle(TestQuery query) {
            throw new RuntimeException("Query handler failed");
        }
    }

    static class NullReturningQueryHandler implements QueryHandler<TestQuery, String> {
        @Override
        public String handle(TestQuery query) {
            return null;
        }
    }

    static class IntegerQueryHandler implements QueryHandler<IntegerQuery, Integer> {
        @Override
        public Integer handle(IntegerQuery query) {
            return 42;
        }
    }

    static class ComplexQueryHandler implements QueryHandler<ComplexQuery, ComplexResult> {
        @Override
        public ComplexResult handle(ComplexQuery query) {
            return new ComplexResult(query.value(), query.value().length());
        }
    }

    static class EmptyQueryHandler implements QueryHandler<EmptyQuery, String> {
        @Override
        public String handle(EmptyQuery query) {
            return "empty";
        }
    }
}
