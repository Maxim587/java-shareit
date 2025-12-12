package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper mapper = ItemMapper.INSTANCE;

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return mapper.mapToItemDtoList(itemStorage.getUserItems(userId));
    }

    @Override
    public Optional<ItemDto> getItemById(long id) {
        return itemStorage.getItemById(id).map(mapper::mapToItemDto);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text).stream()
                .map(mapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long owner) {
        userStorage.getUserById(owner).orElseThrow(() -> new NotFoundException("Пользователь с id " + owner + " не найден"));
        Item item = mapper.mapToItem(itemDto);
        item.setOwner(owner);
        return mapper.mapToItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(UpdateItemDto itemDto, Long itemId, Long owner) {
        Item item = itemStorage.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("Ошибка обновления предмета. Предмет не найден id:" + itemId));

        updateItemFields(item, itemDto, owner);

        return mapper.mapToItemDto(itemStorage.update(item));
    }

    private void updateItemFields(Item currentItem, UpdateItemDto newItem, Long owner) {
        if (!currentItem.getOwner().equals(owner)) {
            throw new ForbiddenOperationException("Ошибка обновления предмета id:" + currentItem.getId() +
                                                  ". Пользователь с id:" + owner + " не является владельцем предмета");
        }

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            currentItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            currentItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            currentItem.setAvailable(newItem.getAvailable());
        }
    }
}
