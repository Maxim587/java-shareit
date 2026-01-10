package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody NewItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getUserItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestShortDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable long requestId) {
        return requestService.getItemRequestById(requestId);
    }
}
