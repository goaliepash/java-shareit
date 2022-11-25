package ru.practicum.shareit.request.data.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.data.mapper.ItemMapper;
import ru.practicum.shareit.request.data.ItemRequest;
import ru.practicum.shareit.request.data.dto.ItemRequestDto;
import ru.practicum.shareit.user.data.User;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(itemRequestDto.getCreated());
        if (itemRequestDto.getItems() != null) {
            itemRequest.setItems(itemRequestDto.getItems().stream().map(ItemMapper::fromStandardItemDto).collect(Collectors.toSet()));
        }
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems() != null ?
                        itemRequest.getItems().stream().map(item -> ItemMapper.toStandardItemDto(item, null)).collect(Collectors.toSet()) :
                        null
        );
    }
}