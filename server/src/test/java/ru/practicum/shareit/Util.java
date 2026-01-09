package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public final class Util {
    private Util() {
    }

    public static UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    public static ItemDto makeItemDto(Long id, String name, String description, boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }

    public static NewItemRequestDto makeNewItemRequestDto(String description) {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription(description);
        return newItemRequestDto;
    }

    public static NewBookingDto makeNewBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

}
