package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "name", "desc", true, 1L);

        when(itemService.create(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void update() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "name", "desc", true, 1L);
        UpdateItemDto updateItemDto = makeUpdateItemDto("name", "desc", true);

        when(itemService.update(any(UpdateItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = makeCommentDto(1L, "text", 1L, "author", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("text");

        when(itemService.createComment(any(NewCommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", commentDto.getItemId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(newCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }


    @Test
    void getUserItems() throws Exception {
        BookingDatesDto lastBooking1 = makeBookingDatesDto(1L, LocalDateTime.now().minusDays(100).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusDays(99).truncatedTo(ChronoUnit.SECONDS));
        BookingDatesDto lastBooking2 = makeBookingDatesDto(1L, LocalDateTime.now().plusDays(100).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusDays(101).truncatedTo(ChronoUnit.SECONDS));
        BookingDatesDto nextBooking1 = makeBookingDatesDto(2L, LocalDateTime.now().plusDays(50).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusDays(51).truncatedTo(ChronoUnit.SECONDS));
        BookingDatesDto nextBooking2 = makeBookingDatesDto(2L, LocalDateTime.now().plusDays(200).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusDays(201).truncatedTo(ChronoUnit.SECONDS));

        ItemExtendedDto dto1 = makeItemExtendedDto(1L, "name1", "desc1", true, lastBooking1, nextBooking1, 1L);
        ItemExtendedDto dto2 = makeItemExtendedDto(2L, "name2", "desc2", true, lastBooking2, nextBooking2, null);

        Collection<ItemExtendedDto> itemExtendedDtos = Arrays.asList(dto1, dto2);

        when(itemService.getUserItems(1L, 0, 10))
                .thenReturn(itemExtendedDtos);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto1.getName())))
                .andExpect(jsonPath("$[0].description", is(dto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking.id", is(dto1.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start", is(dto1.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].lastBooking.end", is(dto1.getLastBooking().getEnd().toString())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(dto1.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.start", is(dto1.getNextBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].nextBooking.end", is(dto1.getNextBooking().getEnd().toString())))
                .andExpect(jsonPath("$[0].requestId", is(dto1.getRequestId()), Long.class));
    }

    @Test
    void getItemById() throws Exception {
        BookingDatesDto lastBooking1 = makeBookingDatesDto(1L, LocalDateTime.now().minusDays(100).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().minusDays(99).truncatedTo(ChronoUnit.SECONDS));
        BookingDatesDto nextBooking1 = makeBookingDatesDto(2L, LocalDateTime.now().plusDays(100).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusDays(101).truncatedTo(ChronoUnit.SECONDS));
        ItemExtendedDto dto1 = makeItemExtendedDto(1L, "name1", "desc1", true, lastBooking1, nextBooking1, 1L);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(dto1);

        mvc.perform(get("/items/{itemId}", dto1.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto1.getName())))
                .andExpect(jsonPath("$.description", is(dto1.getDescription())))
                .andExpect(jsonPath("$.available", is(dto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking.id", is(dto1.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.start", is(dto1.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$.lastBooking.end", is(dto1.getLastBooking().getEnd().toString())))
                .andExpect(jsonPath("$.nextBooking.id", is(dto1.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.start", is(dto1.getNextBooking().getStart().toString())))
                .andExpect(jsonPath("$.nextBooking.end", is(dto1.getNextBooking().getEnd().toString())))
                .andExpect(jsonPath("$.requestId", is(dto1.getRequestId()), Long.class));
    }

    @Test
    void search() throws Exception {
        ItemDto dto1 = makeItemDto(1L, "name1", "desc1", true, 1L);
        ItemDto dto2 = makeItemDto(2L, "name2", "desc3", true, null);
        List<ItemDto> itemDtos = Arrays.asList(dto1, dto2);

        when(itemService.search(anyString()))
                .thenReturn(itemDtos);

        mvc.perform(get("/items/search")
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto1.getName())))
                .andExpect(jsonPath("$[0].description", is(dto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(dto1.getRequestId()), Long.class));
    }

    private ItemExtendedDto makeItemExtendedDto(Long id,
                                                String name,
                                                String description,
                                                boolean available,
                                                BookingDatesDto lastBooking,
                                                BookingDatesDto nextBooking,
                                                Long requestId) {
        ItemExtendedDto dto = new ItemExtendedDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setRequestId(requestId);
        return dto;
    }

    private ItemDto makeItemDto(Long id, String name, String description, boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }

    private UpdateItemDto makeUpdateItemDto(String name, String description, boolean available) {
        UpdateItemDto dto = new UpdateItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private CommentDto makeCommentDto(Long id, String text, Long itemId, String authorName, LocalDateTime created) {
        CommentDto dto = new CommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setItemId(itemId);
        dto.setAuthorName(authorName);
        dto.setCreated(created);
        return dto;
    }

    private BookingDatesDto makeBookingDatesDto(Long id, LocalDateTime start, LocalDateTime end) {
        BookingDatesDto dto = new BookingDatesDto();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }
}
