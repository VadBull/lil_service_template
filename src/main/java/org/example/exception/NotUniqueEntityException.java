package org.example.exception;

public class NotUniqueEntityException extends RuntimeException{

    public NotUniqueEntityException(String message) {
        super(message);
    }

    public NotUniqueEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
