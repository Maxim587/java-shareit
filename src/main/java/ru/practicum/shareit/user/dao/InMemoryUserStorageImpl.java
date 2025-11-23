package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        return Optional.ofNullable(users.computeIfPresent(user.getId(), (id, oldUser) -> user))
                .orElseThrow(() -> new NotFoundException("Ошибка обновления пользователя. Пользователь не найден id:" + user.getId()));
    }

    @Override
    public boolean delete(long id) {
        return users.remove(id) != null;
    }

    @Override
    public boolean isUniqueEmail(String email) {
        return users.values().stream().noneMatch(user -> user.getEmail().equals(email));
    }
}
