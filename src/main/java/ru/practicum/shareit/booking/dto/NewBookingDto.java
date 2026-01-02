package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingDto {
    @NotNull(message = "Значение не должно быть пустым")
    @FutureOrPresent(message = "Значение не должно быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Значение не должно быть пустым")
    @FutureOrPresent(message = "Значение не должно быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "Значение не должно быть пустым")
    @Positive(message = "Значение должно быть положительным числом")
    private Long itemId;
}
