package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.data.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getByRequesterId(long requesterId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto get(long userId, long requestId);
}