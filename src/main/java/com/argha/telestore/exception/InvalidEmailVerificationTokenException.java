package com.argha.telestore.exception;

public class InvalidEmailVerificationTokenException extends RuntimeException {

    public InvalidEmailVerificationTokenException(String message) {

        super(message);
    }
}
