package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> findUserBookings(Long bookerId, String state, Integer from, Integer size);

    BookingDto create(NewBookingDto bookingDto, Long bookerId);

    BookingDto findBookingById(Long bookingId, Long userId);

    BookingDto approveBookingRequest(Long bookingId, boolean approved, Long userId);

    List<BookingDto> findUserItemsBookings(Long ownerId, String state, Integer from, Integer size);
}
