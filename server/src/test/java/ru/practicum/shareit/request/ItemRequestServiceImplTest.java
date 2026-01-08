package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    void getItemRequests() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        Long requestorId = userService.create(userDto).getId();

        UserDto creatorDto1 = makeUserDto("name1", "mail1@mail.com");
        UserDto creatorDto2 = makeUserDto("name2", "mail2@mail.com");
        Long creatorId1 = userService.create(creatorDto1).getId();
        Long creatorId2 = userService.create(creatorDto2).getId();

        NewItemRequestDto newRequestDto1 = makeNewItemRequestDto("description1");
        NewItemRequestDto newRequestDto2 = makeNewItemRequestDto("description2");
        NewItemRequestDto newRequestDto3 = makeNewItemRequestDto("description3");

        ItemRequestDto requestDtoSaved1 = itemRequestService.create(creatorId1, newRequestDto1);
        ItemRequestDto requestDtoSaved2 = itemRequestService.create(creatorId2, newRequestDto2);
        itemRequestService.create(requestorId, newRequestDto3);

        List<ItemRequestShortDto> targetRequests = itemRequestService.getItemRequests(requestorId, 0, 10);

        assertThat(targetRequests, hasSize(2));
        assertThat(targetRequests, hasItem(allOf(
                hasProperty("id", equalTo(requestDtoSaved1.getId())),
                hasProperty("description", equalTo(requestDtoSaved1.getDescription())),
                hasProperty("requestorId", equalTo(requestDtoSaved1.getRequestorId())),
                hasProperty("created", equalTo(requestDtoSaved1.getCreated()))
        )));
        assertThat(targetRequests, hasItem(allOf(
                hasProperty("id", equalTo(requestDtoSaved2.getId())),
                hasProperty("description", equalTo(requestDtoSaved2.getDescription())),
                hasProperty("requestorId", equalTo(requestDtoSaved2.getRequestorId())),
                hasProperty("created", equalTo(requestDtoSaved2.getCreated()))
        )));
    }

    @Test
    void getUserItemRequests() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        Long requestorId = userService.create(userDto).getId();

        NewItemRequestDto newRequestDto1 = makeNewItemRequestDto("description1");
        NewItemRequestDto newRequestDto2 = makeNewItemRequestDto("description2");

        ItemRequestDto requestDtoSaved1 = itemRequestService.create(requestorId, newRequestDto1);
        ItemRequestDto requestDtoSaved2 = itemRequestService.create(requestorId, newRequestDto2);

        UserDto ownerDto = makeUserDto("name", "email@email.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto1 = makeItemDto(null, "name1", "desc1", true, requestDtoSaved1.getId());
        ItemDto item1 = itemService.create(itemDto1, owner.getId());

        ItemDto itemDto2 = makeItemDto(null, "name2", "desc2", true, requestDtoSaved2.getId());
        ItemDto item2 = itemService.create(itemDto2, owner.getId());

        List<ItemRequestDto> targetRequests = itemRequestService.getUserItemRequests(requestorId, 0, 10);

        assertThat(targetRequests, hasSize(2));
        assertThat(targetRequests, hasItem(allOf(
                hasProperty("id", equalTo(requestDtoSaved1.getId())),
                hasProperty("description", equalTo(requestDtoSaved1.getDescription())),
                hasProperty("requestorId", equalTo(requestDtoSaved1.getRequestorId())),
                hasProperty("created", equalTo(requestDtoSaved1.getCreated()))
        )));
        assertThat(targetRequests, hasItem(allOf(
                hasProperty("id", equalTo(requestDtoSaved2.getId())),
                hasProperty("description", equalTo(requestDtoSaved2.getDescription())),
                hasProperty("requestorId", equalTo(requestDtoSaved2.getRequestorId())),
                hasProperty("created", equalTo(requestDtoSaved2.getCreated()))
        )));
    }

    @Test
    void getItemRequestById() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto user = userService.create(userDto);
        NewItemRequestDto newRequestDto1 = makeNewItemRequestDto("description1");
        ItemRequestDto requestDtoSaved1 = itemRequestService.create(user.getId(), newRequestDto1);

        ItemRequestDto targetRequests = itemRequestService.getItemRequestById(requestDtoSaved1.getId());

        assertThat(targetRequests.getId(), equalTo(requestDtoSaved1.getId()));
        assertThat(targetRequests.getDescription(), equalTo(requestDtoSaved1.getDescription()));
        assertThat(targetRequests.getRequestorId(), equalTo(requestDtoSaved1.getRequestorId()));
        assertThat(targetRequests.getCreated(), equalTo(requestDtoSaved1.getCreated()));

        //wrong request id
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(999L));
    }

    @Test
    void create() {
        NewItemRequestDto newRequestDto = makeNewItemRequestDto("description1");
        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, newRequestDto));
    }

    private NewItemRequestDto makeNewItemRequestDto(String description) {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription(description);
        return newItemRequestDto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
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
}
