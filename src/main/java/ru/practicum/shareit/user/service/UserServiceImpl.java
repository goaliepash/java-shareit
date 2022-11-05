package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public UserDto create(UserDto user) {
        return storage.create(user);
    }

    @Override
    public UserDto update(long userId, UserDto user) {
        return storage.update(userId, user);
    }

    @Override
    public UserDto get(long userId) {
        return storage.get(userId);
    }

    @Override
    public void delete(long userId) {
        storage.delete(userId);
    }

    @Override
    public List<UserDto> get() {
        return storage.get();
    }
}