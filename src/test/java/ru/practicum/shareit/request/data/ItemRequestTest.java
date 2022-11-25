package ru.practicum.shareit.request.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.user.data.User;

import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.shareit.utils.Creator.*;

class ItemRequestTest {

    @Test
    void testEquals() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest1 = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));
        ItemRequest itemRequest2 = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));

        Assertions.assertEquals(itemRequest1, itemRequest2);
    }

    @Test
    void testHashCode() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest1 = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));
        ItemRequest itemRequest2 = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));

        Assertions.assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }
}