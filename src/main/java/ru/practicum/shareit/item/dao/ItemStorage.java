package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> getUserItems(long userId);

    Optional<Item> getItemById(long id);

    List<Item> search(String text);

    Item create(Item item);

    Item update(Item item);
}
