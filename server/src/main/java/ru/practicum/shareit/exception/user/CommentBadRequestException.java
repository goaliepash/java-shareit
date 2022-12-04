package ru.practicum.shareit.exception.user;

public class CommentBadRequestException extends RuntimeException {

    public CommentBadRequestException(String message) {
        super(message);
    }
}