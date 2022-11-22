package ru.practicum.shareit.exception.user;

public class UserConflictException extends RuntimeException {

    public UserConflictException(String message) {
        super(message);
    }
}