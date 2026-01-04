package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    ItemDto mapToItemDto(Item item);

    ItemExtendedDto mapToItemExtendedDto(Item item);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "owner", source = "user")
    Item mapToItem(ItemDto itemDto, User user);

    List<ItemDto> mapToItemDtoList(List<Item> itemList);

    List<ItemExtendedDto> mapToItemExtendedDtoList(List<Item> itemList);
}
