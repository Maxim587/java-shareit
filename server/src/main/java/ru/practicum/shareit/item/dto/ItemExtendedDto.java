package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDatesDto;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemExtendedDto {
    private final List<CommentDto> comments = new ArrayList<>();
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDatesDto lastBooking;
    private BookingDatesDto nextBooking;
    private Long requestId;
}
