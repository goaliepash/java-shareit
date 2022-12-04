package ru.practicum.shareit.item.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ru.practicum.shareit.utils.Creator.createItem;

class ItemTest {

    @Test
    void testEquals() {
        Item createdItem1 = createItem(1L, "Name", "Desc", true, 1L);
        Item createdItem2 = createItem(1L, "Name", "Desc", true, 1L);
        Assertions.assertEquals(createdItem1, createdItem2);
    }

    @Test
    void testHashCode() {
        Item createdItem1 = createItem(1L, "Name", "Desc", true, 1L);
        Item createdItem2 = createItem(1L, "Name", "Desc", true, 1L);
        Assertions.assertEquals(createdItem1.hashCode(), createdItem2.hashCode());
    }
}