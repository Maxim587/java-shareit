package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.findUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.findUserItemsBookings(userId, state, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody NewBookingDto newBookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(newBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBookingRequest(@PathVariable Long bookingId,
                                            @RequestParam boolean approved,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveBookingRequest(bookingId, approved, userId);
    }

}
