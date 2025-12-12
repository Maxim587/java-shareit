package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserDto {
    @Positive(message = "Значение должно быть положительным числом")
    private Long id;

    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String name;

    @NotBlank(message = "Значение не должно быть пустым")
    @Email(message = "Значение должно соответствовать формату email")
    private String email;
}
