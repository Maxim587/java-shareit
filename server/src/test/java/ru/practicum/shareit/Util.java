package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.stream.Collectors;

public final class Util {
    public static final Long NONEXISTENT_ID = 999L;
    public static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDateTime BOOKING_START_DATE_IN_PAST = LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDateTime BOOKING_END_DATE_IN_PAST = LocalDateTime.now().minusDays(9).truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDateTime BOOKING_START_DATE_IN_FUTURE = LocalDateTime.now().plusDays(100).truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDateTime BOOKING_END_DATE_IN_FUTURE = LocalDateTime.now().plusDays(120).truncatedTo(ChronoUnit.SECONDS);

    private Util() {
    }

    public static UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    public static UserDto makeRandomUserDto() {
        return makeUserDto(getRandomString(15), getRandomEmail());
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

    public static String getRandomString(int size) {
        return new Random().ints(size, 97, 123).mapToObj(randomInt -> Character.toString((char) randomInt)).collect(Collectors.joining());
    }

    public static String getRandomEmail() {
        return getRandomString(10) + "@" + getRandomString(5) + ".com";
    }

}
