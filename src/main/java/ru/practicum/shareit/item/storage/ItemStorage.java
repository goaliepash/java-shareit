package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {

    ItemDto create(User user, ItemDto itemDto);

    ItemDto update(long itemId, User owner, ItemDto itemDto);

    ItemDto get(long itemId, User owner);

    List<ItemDto> get(long userId);

    List<ItemDto> search(String text);
}