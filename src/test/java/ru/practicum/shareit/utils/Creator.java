package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.data.Comment;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.request.data.ItemRequest;
import ru.practicum.shareit.user.data.User;

import java.time.LocalDateTime;
import java.util.Set;

public class Creator {

    public static Item createItem(long id, String name, String description, boolean available, long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }

    public static User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public static Comment createComment(long id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }

    public static Booking createBooking(long id, LocalDateTime start, LocalDateTime end, Item item, BookingStatus status, User booker) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setBooker(booker);
        return booking;
    }

    public static ItemRequest createItemRequest(long id, String description, User requester, LocalDateTime created, Set<Item> items) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(created);
        itemRequest.setItems(items);
        return itemRequest;
    }
}
