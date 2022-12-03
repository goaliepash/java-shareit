package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.data.dto.CommentDto;
import ru.practicum.shareit.item.data.dto.CommentRequestDto;
import ru.practicum.shareit.item.data.dto.ItemDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, StandardItemDto itemDto);

    ItemDto update(long itemId, long userId, StandardItemDto itemDto);

    ItemDto get(long itemId, long ownerId);

    List<ItemDto> get(long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(long itemId, long userId, CommentRequestDto commentRequestDto);
}