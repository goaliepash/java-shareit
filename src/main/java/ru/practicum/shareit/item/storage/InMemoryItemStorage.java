package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemForbiddenException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public
class InMemoryItemStorage implements ItemStorage {

    private static long currentIdentifier;
    private final Map<Long, Item> storage = new TreeMap<>(Long::compareTo);

    @Override
    public ItemDto create(User owner, ItemDto itemDto) {
        Item item = Item
                .builder()
                .id(++currentIdentifier)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
        storage.put(currentIdentifier, item);
        return ItemMapper.toItemDto(storage.get(currentIdentifier));
    }

    @Override
    public ItemDto update(long itemId, User owner, ItemDto itemDto) {
        checkIfItemExists(itemId);
        Item updatedItem = storage.get(itemId);
        checkItemOwner(updatedItem, owner);
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(storage.get(itemId));
    }

    @Override
    public ItemDto get(long itemId, User owner) {
        checkIfItemExists(itemId);
        Item item = storage.get(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> get(long userId) {
        return storage
                .values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return storage
                    .values()
                    .stream()
                    .filter(Item::getAvailable)
                    .filter(item ->
                            item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getDescription().toLowerCase().contains(text.toLowerCase())
                    )
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    private void checkIfItemExists(long itemId) {
        if (!storage.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь не найдена.");
        }
    }

    private void checkItemOwner(Item item, User owner) {
        if (!item.getOwner().equals(owner)) {
            throw new ItemForbiddenException("Указан неверный владелец вещи.");
        }
    }
}