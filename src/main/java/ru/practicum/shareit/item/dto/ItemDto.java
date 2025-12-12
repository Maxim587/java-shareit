package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDto {
    @Positive(message = "Значение должно быть положительным числом")
    private Long id;

    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String name;

    @NotNull(message = "Значение не должно быть пустым")
    private String description;

    @NotNull(message = "Значение не должно быть пустым")
    private Boolean available;
}
