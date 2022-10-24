package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(long userId, UserDto user);

    UserDto get(long userId);

    void delete(long userId);

    List<UserDto> get();
}