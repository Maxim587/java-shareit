package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;


    @Test
    void getUserItems() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = userService.create(userDto);

        ItemDto itemDto1 = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto itemDto2 = makeItemDto(null, "name2", "desc2", true, null);
        ItemDto itemDto3 = makeItemDto(null, "name3", "desc3", true, null);
        ItemDto itemDtoSaved1 = itemService.create(itemDto1, userDtoSaved.getId());
        ItemDto itemDtoSaved2 = itemService.create(itemDto2, userDtoSaved.getId());
        ItemDto itemDtoSaved3 = itemService.create(itemDto3, userDtoSaved.getId());

        List<ItemDto> savedDtos = Arrays.asList(itemDtoSaved1, itemDtoSaved2, itemDtoSaved3);

        Collection<ItemExtendedDto> targetDtos = itemService.getUserItems(userDtoSaved.getId(), 0, 10);

        assertThat(targetDtos, hasSize(3));
        for (ItemDto dto : savedDtos) {
            assertThat(targetDtos, hasItem(allOf(
                    hasProperty("id", equalTo(dto.getId())),
                    hasProperty("name", equalTo(dto.getName())),
                    hasProperty("description", equalTo(dto.getDescription())),
                    hasProperty("available", equalTo(dto.getAvailable())),
                    hasProperty("lastBooking", nullValue()),
                    hasProperty("nextBooking", nullValue()),
                    hasProperty("requestId", equalTo(dto.getRequestId())),
                    hasProperty("comments", empty())
            )));
        }
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
