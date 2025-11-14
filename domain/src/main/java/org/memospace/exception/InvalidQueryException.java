package org.memospace.exception;

public class InvalidQueryException extends RuntimeException {
    private final int position;

    public InvalidQueryException(String message, int position) {
        super(message + " at position " + position);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
