package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {
    @Mapping(target = "requestorId", source = "requestor.id")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestorId", source = "requestor.id")
    ItemRequestShortDto mapToItemRequestShortDto(ItemRequest itemRequest);

    @Mapping(target = "requestor", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    ItemRequest mapToItemRequest(NewItemRequestDto newItemRequestDto, User user);
}
