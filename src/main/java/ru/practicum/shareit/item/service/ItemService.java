package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    ItemDto get(long itemId, long userId);

    List<ItemDto> get(long userId);

    List<ItemDto> search(String text);
}