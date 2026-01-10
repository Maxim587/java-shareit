package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemDataDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.practicum.shareit.Util.getRandomString;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper;

    @Test
    void shouldMapItemToItemDataDto() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("name");
        requestor.setEmail("email");

        ItemRequest request = new ItemRequest(1L, "desc", requestor, null);

        User itemOwner = new User();
        itemOwner.setId(2L);
        itemOwner.setName("nameOwner");
        itemOwner.setEmail("emailOwner");

        Item item = new Item();
        item.setId(1L);
        item.setName(getRandomString(10));
        item.setDescription(getRandomString(10));
        item.setAvailable(true);
        item.setOwner(itemOwner);
        item.setRequest(request);

        ItemDataDto itemDataDto = itemRequestMapper.mapToItemResponseDto(item);

        assertThat(itemDataDto, allOf(
                hasProperty("itemId", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("ownerId", equalTo(itemOwner.getId()))
        ));
    }

    @Test
    void shouldMapItemRequestToItemRequestDto() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("name");
        requestor.setEmail("email");

        User itemOwner = new User();
        itemOwner.setId(2L);
        itemOwner.setName("nameOwner");
        itemOwner.setEmail("emailOwner");

        Item item = new Item();
        item.setId(1L);
        item.setName(getRandomString(10));
        item.setDescription(getRandomString(10));
        item.setAvailable(true);
        item.setOwner(itemOwner);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName(getRandomString(10));
        item2.setDescription(getRandomString(10));
        item2.setAvailable(true);
        item2.setOwner(itemOwner);

        List<Item> items = Arrays.asList(item, item2);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(getRandomString(10));
        itemRequest.setRequestor(requestor);
        itemRequest.setItems(items);

        ItemRequestDto itemRequestDto = itemRequestMapper.mapToItemRequestDto(itemRequest);

        assertThat(itemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDto.getRequestorId(), equalTo(itemRequest.getRequestor().getId()));

        for (Item item1 : items) {
            assertThat(itemRequestDto.getItems(), hasItem(allOf(
                    hasProperty("itemId", equalTo(item1.getId())),
                    hasProperty("name", equalTo(item1.getName())),
                    hasProperty("ownerId", equalTo(item1.getOwner().getId()))
            )));
        }
    }
}
