package ru.rayovsky.disp.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuthorizationException(String message) {
        super(message);
    }
}
