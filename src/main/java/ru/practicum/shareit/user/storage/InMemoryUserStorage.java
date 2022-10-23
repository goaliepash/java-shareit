package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private static long currentIdentifier;
    private final Map<Long, User> storage = new TreeMap<>(Long::compare);

    @Override
    public User create(User user) {
        checkIfUserWithThisEmailExists(user.getEmail());
        user.setId(++currentIdentifier);
        storage.put(currentIdentifier, user);
        return storage.get(currentIdentifier);
    }

    @Override
    public User update(long userId, User user) {
        checkIfUserExists(userId);
        User updatedUser = storage.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkIfUserWithThisEmailExists(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        return storage.get(userId);
    }

    @Override
    public User get(long userId) {
        checkIfUserExists(userId);
        return storage.get(userId);
    }

    @Override
    public void delete(long userId) {
        storage.remove(userId);
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(storage.values());
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