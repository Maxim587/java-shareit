package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {
    private String id;
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
