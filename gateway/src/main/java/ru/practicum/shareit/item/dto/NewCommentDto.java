package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Значение не должно быть пустым")
    private String text;
}
