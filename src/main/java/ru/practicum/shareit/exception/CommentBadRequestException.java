package ru.practicum.shareit.exception;

public class CommentBadRequestException extends RuntimeException {

    public CommentBadRequestException(String message) {
        super(message);
    }
}