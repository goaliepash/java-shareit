package ru.practicum.shareit.exception.request;

public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}