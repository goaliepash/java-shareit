package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private long currentIdentifier;

    private final Map<Long, User> storage = new TreeMap<>(Long::compare);

    @Override
    public UserDto create(UserDto user) {
        checkIfUserWithThisEmailExists(user.getEmail());
        User createdUser = User
                .builder()
                .id(++currentIdentifier)
                .name(user.getName())
                .email(user.getEmail())
                .build();
        storage.put(currentIdentifier, createdUser);
        return UserMapper.toUserDto(storage.get(currentIdentifier));
    }

    @Override
    public UserDto update(long userId, UserDto user) {
        checkIfUserExists(userId);
        User updatedUser = storage.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkIfUserWithThisEmailExists(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(storage.get(userId));
    }

    @Override
    public UserDto get(long userId) {
        checkIfUserExists(userId);
        return UserMapper.toUserDto(storage.get(userId));
    }

    @Override
    public void delete(long userId) {
        storage.remove(userId);
    }

    @Override
    public List<UserDto> get() {
        return storage
                .values()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(long userId) {
        return storage.containsKey(userId);
    }

    private void checkIfUserWithThisEmailExists(String email) {
        if (storage.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new UserConflictException("Пользователь с таким e-mail уже существует.");
        }
    }

    private void checkIfUserExists(long userId) {
        if (!storage.containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }
}