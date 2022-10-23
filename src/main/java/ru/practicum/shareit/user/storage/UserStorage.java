package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(long userId, User user);

    User get(long userId);

    void delete(long userId);

    List<User> get();

    boolean contains(long userId);
}