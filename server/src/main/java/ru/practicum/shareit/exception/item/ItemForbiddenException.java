package ru.practicum.shareit.exception.item;

public class ItemForbiddenException extends RuntimeException {

    public ItemForbiddenException(String message) {
        super(message);
    }
}