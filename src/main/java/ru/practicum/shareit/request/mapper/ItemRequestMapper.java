package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {
    @Mapping(target = "requestor", source = "requestor.id")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);


    @Mapping(target = "id", source = "itemRequestDto.id")
    @Mapping(target = "requestor", source = "user")
    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user);
}
