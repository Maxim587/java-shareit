package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemExtendedDto> getUserItems(Long userId);

    ItemExtendedDto getItemById(Long itemId, Long userId);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto itemDto, Long owner);

    ItemDto update(UpdateItemDto itemDto, Long itemId, Long owner);

    CommentDto createComment(NewCommentDto commentDto, Long itemId, Long userId);
}
