package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    @NotBlank(message = "Значение не должно быть пустым")
    private String name;

    @NotNull(message = "Поле является обязательным")
    private String description;

    @NotNull(message = "Поле является обязательным")
    private Boolean available;

    private Long requestId;
}
