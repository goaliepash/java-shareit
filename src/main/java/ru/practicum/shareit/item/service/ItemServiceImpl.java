package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        checkIfUserExists(userId);
        User owner = UserMapper.fromUserDto(userStorage.get(userId));
        return itemStorage.create(owner, itemDto);

    }

    @Override
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        checkIfUserExists(userId);
        User owner = UserMapper.fromUserDto(userStorage.get(userId));
        return itemStorage.update(itemId, owner, itemDto);
    }

    @Override
    public ItemDto get(long itemId, long userId) {
        checkIfUserExists(userId);
        User owner = UserMapper.fromUserDto(userStorage.get(userId));
        return itemStorage.get(itemId, owner);
    }

    @Override
    public List<ItemDto> get(long userId) {
        checkIfUserExists(userId);
        return itemStorage.get(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text);
    }

    private void checkIfUserExists(long userId) {
        if (!userStorage.contains(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }
}