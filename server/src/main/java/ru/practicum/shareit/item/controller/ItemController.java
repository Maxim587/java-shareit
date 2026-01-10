package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemExtendedDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto getItemById(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody UpdateItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody NewCommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
