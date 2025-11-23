package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDto> getUserItems(long userId);

    Optional<ItemDto> getItemById(long id);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto itemDto, Long owner);

    ItemDto update(UpdateItemDto itemDto, Long itemId, Long owner);
}
