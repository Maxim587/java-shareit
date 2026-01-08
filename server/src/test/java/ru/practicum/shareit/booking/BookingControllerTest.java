package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;


    @Test
    void create() throws Exception {
        NewBookingDto newBookingDto = makeNewBookingDto(1L,
                LocalDateTime.now().plusDays(1L).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(10L).truncatedTo(ChronoUnit.SECONDS)
        );
        BookingDto dto = makeBookingDto(1L, makeItemDto(), makeUserDto(), "APPROVED", newBookingDto.getStart(), newBookingDto.getEnd());

        when(bookingService.create(ArgumentMatchers.any(NewBookingDto.class), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(newBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(dto.getStatus())));
//                .andExpect(jsonPath("$.start", is(dto.getStart().toString())))
//                .andExpect(jsonPath("$.end", is(dto.getEnd().toString())));
    }

    @Test
    void approveBookingRequest() throws Exception {
        BookingDto dto = makeBookingDto(1L, makeItemDto(), makeUserDto(), "APPROVED",
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(11L).truncatedTo(ChronoUnit.SECONDS)
        );

        when(bookingService.approveBookingRequest(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(dto);

        mvc.perform(patch("/bookings/{bookingId}", dto.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(dto.getStatus())));
//                .andExpect(jsonPath("$.start", is(dto.getStart().toString())))
//                .andExpect(jsonPath("$.end", is(dto.getEnd().toString())));
    }

    @Test
    void findUserBookings() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS);
        BookingDto dto1 = makeBookingDto(1L, makeItemDto(), makeUserDto(), "APPROVED", start, start.plusDays(1L));
        BookingDto dto2 = makeBookingDto(2L, makeItemDto(), makeUserDto(), "APPROVED", start.plusDays(10L), start.plusDays(11L));

        List<BookingDto> dtos = Arrays.asList(dto1, dto2);

        when(bookingService.findUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(dtos);

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(dto1.getStatus())));
//                .andExpect(jsonPath("$[0].start", is(dto1.getStart().toString())))
//                .andExpect(jsonPath("$[0].end", is(dto1.getEnd().toString())));
    }

    @Test
    void findBookingById() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS);
        BookingDto dto = makeBookingDto(1L, makeItemDto(), makeUserDto(), "APPROVED", start, start.plusDays(1L));

        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/bookings/{bookingId}", dto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(dto.getStatus())));
//                .andExpect(jsonPath("$.start", is(dto.getStart().toString())))
//                .andExpect(jsonPath("$.end", is(dto.getEnd().toString())));
    }

    @Test
    void findUserItemsBookings() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS);
        BookingDto dto1 = makeBookingDto(1L, makeItemDto(), makeUserDto(), "APPROVED", start, start.plusDays(1L));
        BookingDto dto2 = makeBookingDto(2L, makeItemDto(), makeUserDto(), "APPROVED", start.plusDays(10L), start.plusDays(11L));
        List<BookingDto> dtos = Arrays.asList(dto1, dto2);

        when(bookingService.findUserItemsBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(dtos);

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(dto1.getStatus())));
//                .andExpect(jsonPath("$[0].start", is(dto1.getStart().toString())))
//                .andExpect(jsonPath("$[0].end", is(dto1.getEnd().toString())));
    }

    private BookingDto makeBookingDto(Long id, ItemDto item, UserDto booker, String status, LocalDateTime start, LocalDateTime end) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setItem(item);
        dto.setBooker(booker);
        dto.setStatus(status);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    private NewBookingDto makeNewBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    private UserDto makeUserDto() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email");
        return user;
    }

    private ItemDto makeItemDto() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setAvailable(true);
        dto.setRequestId(null);
        return dto;
    }
}
