package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "requestId", source = "request.id")
    ItemDto mapToItemDto(Item item);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "request.id")
    ItemExtendedDto mapToItemExtendedDto(Item item);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "description", source = "itemDto.description")
    Item mapToItem(ItemDto itemDto, User user, ItemRequest request);

    List<ItemDto> mapToItemDtoList(List<Item> itemList);

    List<ItemExtendedDto> mapToItemExtendedDtoList(List<Item> itemList);
}
