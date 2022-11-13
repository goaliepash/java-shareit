package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.data.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto get(long userId);

    void delete(long userId);

    List<UserDto> get();
}