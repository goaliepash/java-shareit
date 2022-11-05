package ru.practicum.shareit.exception;

public class ItemForbiddenException extends RuntimeException {

    public ItemForbiddenException(String message) {
        super(message);
    }
}