package ru.practicum.shareit.user.data;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utils.Creator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void testEquals() {
        User user1 = Creator.createUser(1L, "name", "email@mail.ru");
        User user2 = Creator.createUser(1L, "name", "email@mail.ru");
        assertEquals(user1, user2);
    }

    @Test
    void testHashCode() {
        User user1 = Creator.createUser(1L, "name", "email@mail.ru");
        User user2 = Creator.createUser(1L, "name", "email@mail.ru");
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}