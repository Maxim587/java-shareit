package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDataDto {
    private Long itemId;
    private String name;
    private Long ownerId;
}
