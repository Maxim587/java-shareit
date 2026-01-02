package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentDto {
    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String text;
}
