package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDatesDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
}
