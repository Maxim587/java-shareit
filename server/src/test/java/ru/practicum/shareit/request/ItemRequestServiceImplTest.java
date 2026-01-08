package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

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
}
