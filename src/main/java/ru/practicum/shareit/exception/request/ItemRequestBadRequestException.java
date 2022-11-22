package ru.practicum.shareit.exception.request;

public class ItemRequestBadRequestException extends RuntimeException {

    public ItemRequestBadRequestException(String message) {
        super(message);
    }
}