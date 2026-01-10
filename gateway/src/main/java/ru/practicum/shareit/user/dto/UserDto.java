package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "Значение не должно быть пустым")
    private String name;

    @NotBlank(message = "Значение не должно быть пустым")
    @Email(message = "Значение должно соответствовать формату email")
    private String email;

    private Long requestId;
}
