package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> getUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.isAvailable() &&
                                (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                 item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }

    @Override
    public Item create(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return Optional.ofNullable(items.computeIfPresent(item.getId(), (id, oldItem) -> item))
                .orElseThrow(() -> new NotFoundException("Ошибка обновления предмета. Предмет не найден id:" + item.getId()));
    }
}
