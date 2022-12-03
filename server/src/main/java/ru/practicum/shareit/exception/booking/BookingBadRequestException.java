package ru.practicum.shareit.exception.booking;

public class BookingBadRequestException extends RuntimeException {

    public BookingBadRequestException(String message) {
        super(message);
    }
}