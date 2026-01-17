package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    BookingDto mapToBookingDto(Booking booking);

    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking mapToBooking(NewBookingDto newBookingDto, User user, Item item);

    List<BookingDto> mapToBookingDtoList(Iterable<Booking> bookings);

    BookingDatesDto mapToBookingDatesDto(Booking booking);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto mapToItemDto(Item item);
}
