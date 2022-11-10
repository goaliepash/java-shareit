package ru.practicum.shareit.exception;

public class ItemBadRequestException extends RuntimeException {

    public ItemBadRequestException(String message) {
        super(message);
    }
}