package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, NewItemRequestDto requestDto);

    List<ItemRequestDto> getUserItemRequests(long userId, Integer from, Integer size);

    List<ItemRequestShortDto> getItemRequests(long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(long requestId);
}
