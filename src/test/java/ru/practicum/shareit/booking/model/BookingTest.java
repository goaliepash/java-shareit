package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.user.data.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utils.Creator.*;

class BookingTest {

    @Test
    void testEquals() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );
        Booking booking2 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        assertEquals(booking1, booking2);
    }

    @Test
    void testHashCode() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );
        Booking booking2 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}