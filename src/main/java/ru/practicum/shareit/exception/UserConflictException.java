package ru.practicum.shareit.exception;

public class UserConflictException extends RuntimeException {

    public UserConflictException(String message) {
        super(message);
    }
}