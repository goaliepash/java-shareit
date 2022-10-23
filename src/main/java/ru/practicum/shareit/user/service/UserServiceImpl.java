package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(long userId, User user) {
        return storage.update(userId, user);
    }

    public User get(long userId) {
        return storage.get(userId);
    }

    public void delete(long userId) {
        storage.delete(userId);
    }

    public List<User> get() {
        return storage.get();
    }
}