package org.project.memospace.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.application.service.Command;
import org.project.memospace.application.service.CommandHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleCommandBusTest {

    private SimpleCommandBus commandBus;

    @BeforeEach
    void setUp() {
        commandBus = new SimpleCommandBus();
    }

    @Test
    void shouldRegisterAndExecuteCommandHandler() {
        // Given
        TestCommand command = new TestCommand("test");
        CommandHandler<TestCommand, String> handler = spy(new TestCommandHandler());
        commandBus.registerHandler(TestCommand.class, handler);

        // When
        String result = commandBus.send(command);

        // Then
        assertEquals("handled: test", result);
        verify(handler).handle(command);
    }

    @Test
    void shouldThrowExceptionWhenHandlerNotRegistered() {
        // Given
        TestCommand command = new TestCommand("test");

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commandBus.send(command));
        assertTrue(exception.getMessage().contains("No handler registered for command"));
        assertTrue(exception.getMessage().contains("TestCommand"));
    }

    @Test
    void shouldRegisterMultipleHandlers() {
        // Given
        TestCommand command1 = new TestCommand("test1");
        AnotherTestCommand command2 = new AnotherTestCommand("test2");

        CommandHandler<TestCommand, String> handler1 = new TestCommandHandler();
        CommandHandler<AnotherTestCommand, Integer> handler2 = new AnotherTestCommandHandler();

        commandBus.registerHandler(TestCommand.class, handler1);
        commandBus.registerHandler(AnotherTestCommand.class, handler2);

        // When
        String result1 = commandBus.send(command1);
        Integer result2 = commandBus.send(command2);

        // Then
        assertEquals("handled: test1", result1);
        assertEquals(5, result2);
    }

    @Test
    void shouldOverrideHandlerWhenRegisteredTwice() {
        // Given
        TestCommand command = new TestCommand("test");
        CommandHandler<TestCommand, String> originalHandler = new TestCommandHandler();
        CommandHandler<TestCommand, String> newHandler = spy(new CustomTestCommandHandler());

        commandBus.registerHandler(TestCommand.class, originalHandler);
        commandBus.registerHandler(TestCommand.class, newHandler);

        // When
        String result = commandBus.send(command);

        // Then
        assertEquals("custom: test", result);
        verify(newHandler).handle(command);
    }

    @Test
    void shouldHandleVoidCommands() {
        // Given
        VoidCommand command = new VoidCommand();
        CommandHandler<VoidCommand, Void> handler = spy(new VoidCommandHandler());
        commandBus.registerHandler(VoidCommand.class, handler);

        // When
        Void result = commandBus.send(command);

        // Then
        assertNull(result);
        verify(handler).handle(command);
    }

    @Test
    void shouldPropagateExceptionsFromHandler() {
        // Given
        TestCommand command = new TestCommand("fail");
        CommandHandler<TestCommand, String> handler = new FailingCommandHandler();
        commandBus.registerHandler(TestCommand.class, handler);

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commandBus.send(command));
        assertEquals("Command handler failed", exception.getMessage());
    }

    @Test
    void shouldHandleNullReturnValue() {
        // Given
        TestCommand command = new TestCommand("test");
        CommandHandler<TestCommand, String> handler = new NullReturningCommandHandler();
        commandBus.registerHandler(TestCommand.class, handler);

        // When
        String result = commandBus.send(command);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleDifferentReturnTypes() {
        // Given
        IntegerCommand command = new IntegerCommand();
        CommandHandler<IntegerCommand, Integer> handler = new IntegerCommandHandler();
        commandBus.registerHandler(IntegerCommand.class, handler);

        // When
        Integer result = commandBus.send(command);

        // Then
        assertEquals(42, result);
    }

    // Test commands and handlers

    record TestCommand(String value) implements Command {
    }

    record AnotherTestCommand(String value) implements Command {
    }

    record VoidCommand() implements Command {
    }

    record IntegerCommand() implements Command {
    }

    static class TestCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            return "handled: " + command.value();
        }
    }

    static class CustomTestCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            return "custom: " + command.value();
        }
    }

    static class AnotherTestCommandHandler implements CommandHandler<AnotherTestCommand, Integer> {
        @Override
        public Integer handle(AnotherTestCommand command) {
            return command.value().length();
        }
    }

    static class VoidCommandHandler implements CommandHandler<VoidCommand, Void> {
        @Override
        public Void handle(VoidCommand command) {
            return null;
        }
    }

    static class FailingCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            throw new RuntimeException("Command handler failed");
        }
    }

    static class NullReturningCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            return null;
        }
    }

    static class IntegerCommandHandler implements CommandHandler<IntegerCommand, Integer> {
        @Override
        public Integer handle(IntegerCommand command) {
            return 42;
        }
    }
}
