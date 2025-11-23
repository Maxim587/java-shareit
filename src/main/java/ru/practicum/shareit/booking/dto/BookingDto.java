package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    @Positive(message = "Значение должно быть положительным числом")
    private Long id;

    @NotNull(message = "Значение не должно быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Значение не должно быть пустым")
    private LocalDateTime end;

    @NotNull(message = "Значение не должно быть пустым")
    @Positive(message = "Значение должно быть положительным числом")
    private Long item;

    @NotNull(message = "Значение не должно быть пустым")
    @Positive(message = "Значение должно быть положительным числом")
    private Long booker;

    private String status;
}
