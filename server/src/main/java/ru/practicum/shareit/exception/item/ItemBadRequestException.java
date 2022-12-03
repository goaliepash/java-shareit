package ru.practicum.shareit.exception.item;

public class ItemBadRequestException extends RuntimeException {

    public ItemBadRequestException(String message) {
        super(message);
    }
}