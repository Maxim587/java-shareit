package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private LocalDateTime created;
    @Positive(message = "Значение должно быть положительным числом")
    private Long id;
    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String description;
    @Positive(message = "Значение должно быть положительным числом")
    private Long requestor;
}
