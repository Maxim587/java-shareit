package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(NewBookingDto bookingDto, Long bookerId);

    BookingDto approveBookingRequest(Long bookingId, boolean approved, Long userId);

    BookingDto findBookingById(Long bookingId, Long userId);

    List<BookingDto> findUserBookings(Long bookerId, String state);

    List<BookingDto> findUserItemsBookings(Long ownerId, String state);
}
