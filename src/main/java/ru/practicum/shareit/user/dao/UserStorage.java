package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    Optional<User> getUserById(long id);

    User create(User user);

    User update(User user);

    boolean delete(long id);

    boolean isUniqueEmail(String email);
}
