package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemDataDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        NewItemRequestDto newDto = makeNewItemRequestDto();
        ItemRequestDto responseDto = makeItemRequestDto(1L, "desc", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Collections.emptyList());

        when(itemRequestService.create(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(responseDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(responseDto.getItems()), List.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        ItemRequestDto responseDto = makeItemRequestDto(1L, "desc", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Collections.emptyList());
        when(itemRequestService.getItemRequestById(anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/requests/{id}", responseDto.getRequestorId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(responseDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(responseDto.getItems()), List.class));
    }

    @Test
    void getUserItemRequests() throws Exception {
        ItemDataDto item = new ItemDataDto();
        item.setItemId(1L);
        item.setOwnerId(2L);
        item.setName("item_name");
        List<ItemDataDto> itemDtos = Arrays.asList(item);

        ItemRequestDto itemRequestDto1 = makeItemRequestDto(1L, "desc1", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = makeItemRequestDto(2L, "desc2", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), itemDtos);

        List<ItemRequestDto> itemRequestDtos = Arrays.asList(itemRequestDto1, itemRequestDto2);

        when(itemRequestService.getUserItemRequests(1L, 0, 10))
                .thenReturn(itemRequestDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestDto1.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", empty()))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[1].items[0].itemId", is(item.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].items[0].ownerId", is(item.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[1].items[0].name", is(item.getName())));
    }

    @Test
    void getItemRequests() throws Exception {
        ItemRequestShortDto itemRequestShortDto1 = makeItemRequestShortDto(1L, "desc1", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ItemRequestShortDto itemRequestShortDto2 = makeItemRequestShortDto(2L, "desc2", 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        List<ItemRequestShortDto> itemRequestShortDtos = Arrays.asList(itemRequestShortDto1, itemRequestShortDto2);

        when(itemRequestService.getItemRequests(1L, 0, 10))
                .thenReturn(itemRequestShortDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id", is(itemRequestShortDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestShortDto2.getDescription())))
                .andExpect(jsonPath("$[1].requestorId", is(itemRequestShortDto2.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[1].created", containsString(itemRequestShortDto2.getCreated().toString())));
    }

    private NewItemRequestDto makeNewItemRequestDto() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("description");
        return newItemRequestDto;
    }

    private ItemRequestDto makeItemRequestDto(Long id, String description, Long requestorId, LocalDateTime created, List<ItemDataDto> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequestorId(requestorId);
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    private ItemRequestShortDto makeItemRequestShortDto(Long id, String description, Long requestorId, LocalDateTime created) {
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto();
        itemRequestShortDto.setId(id);
        itemRequestShortDto.setDescription(description);
        itemRequestShortDto.setRequestorId(requestorId);
        itemRequestShortDto.setCreated(created);
        return itemRequestShortDto;
    }
}
