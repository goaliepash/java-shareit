package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto create(UserDto user);

    UserDto update(long userId, UserDto user);

    UserDto get(long userId);

    void delete(long userId);

    List<UserDto> get();

    boolean contains(long userId);
}