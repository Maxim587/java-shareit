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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;


    @Test
    void getItemRequestsShouldReturnItemRequestsOfAllUsersExcludingCreatedByCurrentUser() {
        Long requestorId = userService.create(makeRandomUserDto()).getId();
        Long creatorId1 = userService.create(makeRandomUserDto()).getId();
        Long creatorId2 = userService.create(makeRandomUserDto()).getId();

        ItemRequestDto requestDtoSaved1 = itemRequestService.create(creatorId1, makeNewItemRequestDto("description1"));
        ItemRequestDto requestDtoSaved2 = itemRequestService.create(creatorId2, makeNewItemRequestDto("description2"));
        List<ItemRequestDto> sourceRequests = Arrays.asList(requestDtoSaved1, requestDtoSaved2);

        List<ItemRequestShortDto> targetRequests = itemRequestService.getItemRequests(requestorId, 0, 10);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", equalTo(sourceRequest.getId())),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("requestorId", equalTo(sourceRequest.getRequestorId())),
                    hasProperty("created", equalTo(sourceRequest.getCreated()))
            )));
        }
    }

    @Test
    void getItemRequestsShouldNotReturnItemRequestsOfCurrentUser() {
        Long requestorId = userService.create(makeRandomUserDto()).getId();
        itemRequestService.create(requestorId, makeNewItemRequestDto("description1"));
        List<ItemRequestShortDto> targetRequests = itemRequestService.getItemRequests(requestorId, 0, 10);

        assertThat(targetRequests, hasSize(0));
    }

    @Test
    void getUserItemRequestsShouldReturnAllItemRequestsOfCurrentUser() {
        Long requestorId = userService.create(makeRandomUserDto()).getId();
        ItemRequestDto requestDtoSaved1 = itemRequestService.create(requestorId, makeNewItemRequestDto("description1"));
        ItemRequestDto requestDtoSaved2 = itemRequestService.create(requestorId, makeNewItemRequestDto("description2"));
        List<ItemRequestDto> sourceRequests = Arrays.asList(requestDtoSaved1, requestDtoSaved2);

        UserDto itemOwner = userService.create(makeRandomUserDto());
        ItemDto itemDto1 = makeItemDto(null, "name1", "desc1", true, requestDtoSaved1.getId());
        itemService.create(itemDto1, itemOwner.getId());

        ItemDto itemDto2 = makeItemDto(null, "name2", "desc2", true, requestDtoSaved2.getId());
        itemService.create(itemDto2, itemOwner.getId());

        List<ItemRequestDto> targetRequests = itemRequestService.getUserItemRequests(requestorId, 0, 10);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", equalTo(sourceRequest.getId())),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("requestorId", equalTo(sourceRequest.getRequestorId())),
                    hasProperty("created", equalTo(sourceRequest.getCreated()))
            )));
        }
    }

    @Test
    void getItemRequestByIdWhenCorrectRequestIdShouldReturnItemRequest() {
        UserDto requestor = userService.create(makeRandomUserDto());
        ItemRequestDto request = itemRequestService.create(requestor.getId(), makeNewItemRequestDto("description"));
        UserDto itemOwner = userService.create(makeRandomUserDto());
        ItemDto itemDto = makeItemDto(null, "name", "desc", false, request.getId());
        itemService.create(itemDto, itemOwner.getId());

        ItemRequestDto targetRequests = itemRequestService.getItemRequestById(request.getId());

        assertThat(targetRequests.getId(), equalTo(request.getId()));
        assertThat(targetRequests.getDescription(), equalTo(request.getDescription()));
        assertThat(targetRequests.getRequestorId(), equalTo(request.getRequestorId()));
        assertThat(targetRequests.getCreated(), equalTo(request.getCreated()));
    }

    @Test
    void getItemRequestByIdWhenNonexistentRequestIdShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(NONEXISTENT_ID));
    }

    @Test
    void createItemRequestWhenUserNotFoundShouldThrowNotFoundException() {
        NewItemRequestDto newRequestDto = makeNewItemRequestDto("description1");
        assertThrows(NotFoundException.class, () -> itemRequestService.create(NONEXISTENT_ID, newRequestDto));
    }
}