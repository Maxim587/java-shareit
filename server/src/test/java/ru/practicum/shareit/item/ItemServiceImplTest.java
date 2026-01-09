package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.makeItemDto;
import static ru.practicum.shareit.Util.makeUserDto;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


    @Test
    void getUserItems() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = userService.create(userDto);

        ItemDto itemDto1 = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto itemDto2 = makeItemDto(null, "name2", "desc2", true, null);
        ItemDto itemDto3 = makeItemDto(null, "name3", "desc3", true, 1L);
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
                    hasProperty("comments")
            )));
        }
    }

    @Test
    void getItemById() {
        UserDto ownerDto = makeUserDto("name", "email@email.com");
        UserDto owner = userService.create(ownerDto);

        UserDto bookerDto = makeUserDto("name2", "email2@email.com");
        UserDto booker = userService.create(bookerDto);

        ItemDto itemDto = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto item = itemService.create(itemDto, owner.getId());

        NewBookingDto newBookingDto1 = new NewBookingDto();
        newBookingDto1.setItemId(item.getId());
        newBookingDto1.setStart(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));
        newBookingDto1.setEnd(LocalDateTime.now().minusDays(9).truncatedTo(ChronoUnit.SECONDS));
        BookingDto lastBooking = bookingService.create(newBookingDto1, booker.getId());
        bookingService.approveBookingRequest(lastBooking.getId(), true, owner.getId());

        NewBookingDto newBookingDto2 = new NewBookingDto();
        newBookingDto2.setItemId(item.getId());
        newBookingDto2.setStart(LocalDateTime.now().plusDays(100).truncatedTo(ChronoUnit.SECONDS));
        newBookingDto2.setEnd(LocalDateTime.now().plusDays(120).truncatedTo(ChronoUnit.SECONDS));
        BookingDto nextBooking = bookingService.create(newBookingDto2, booker.getId());
        bookingService.approveBookingRequest(nextBooking.getId(), true, owner.getId());

        NewCommentDto newCommentDto1 = new NewCommentDto();
        newCommentDto1.setText("some text");
        CommentDto comment1 = itemService.createComment(newCommentDto1, item.getId(), booker.getId());

        ItemExtendedDto itemExtendedDto = itemService.getItemById(item.getId(), owner.getId());
        assertThat(itemExtendedDto.getId(), equalTo(item.getId()));
        assertThat(itemExtendedDto.getName(), equalTo(item.getName()));
        assertThat(itemExtendedDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemExtendedDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemExtendedDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemExtendedDto.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(itemExtendedDto.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(itemExtendedDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(itemExtendedDto.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(itemExtendedDto.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(itemExtendedDto.getRequestId(), equalTo(item.getRequestId()));
        assertThat(itemExtendedDto.getComments().getFirst().getId(), equalTo(comment1.getId()));
        assertThat(itemExtendedDto.getComments().getFirst().getText(), equalTo(comment1.getText()));
        assertThat(itemExtendedDto.getComments().getFirst().getItemId(), equalTo(comment1.getItemId()));
        assertThat(itemExtendedDto.getComments().getFirst().getAuthorName(), equalTo(comment1.getAuthorName()));
        assertThat(itemExtendedDto.getComments().getFirst().getCreated(), equalTo(comment1.getCreated()));

        //wrong item
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L, owner.getId()));
    }

    @Test
    void update() {
        UserDto ownerDto = makeUserDto("name", "email@email.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto item = itemService.create(itemDto, owner.getId());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("New name");
        updateItemDto.setDescription("New description");
        updateItemDto.setAvailable(true);

        ItemDto updatedItem = itemService.update(updateItemDto, item.getId(), owner.getId());

        assertThat(updatedItem.getId(), equalTo(item.getId()));
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));

        //blank name
        updateItemDto.setName("");
        assertThrows(ConditionsNotMetException.class, () -> itemService.update(updateItemDto, item.getId(), owner.getId()));

        //wrong owner
        updateItemDto.setName("New name");
        assertThrows(ForbiddenOperationException.class, () -> itemService.update(updateItemDto, item.getId(), 999L));

        //wrong item
        assertThrows(NotFoundException.class, () -> itemService.update(updateItemDto, 999L, owner.getId()));
    }

    @Test
    void updateWithNullValues() {
        UserDto ownerDto = makeUserDto("name", "email@email.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto item = itemService.create(itemDto, owner.getId());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName(null);
        updateItemDto.setDescription(null);
        updateItemDto.setAvailable(null);

        ItemDto updatedItem = itemService.update(updateItemDto, item.getId(), owner.getId());

        assertThat(updatedItem.getId(), equalTo(item.getId()));
        assertThat(updatedItem.getName(), equalTo(item.getName()));
        assertThat(updatedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void search() {
        UserDto ownerDto = makeUserDto("name", "email@email.com");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto1 = makeItemDto(null, "name1", "desc1", true, null);
        ItemDto item1 = itemService.create(itemDto1, owner.getId());

        ItemDto itemDto2 = makeItemDto(null, "1234", "name2", true, null);
        ItemDto item2 = itemService.create(itemDto2, owner.getId());

        List<ItemDto> savedDtos = Arrays.asList(item1, item2);
        List<ItemDto> targetDtos = itemService.search("name");

        for (ItemDto dto : savedDtos) {
            assertThat(targetDtos, hasItem(allOf(
                    hasProperty("id", equalTo(dto.getId())),
                    hasProperty("name", equalTo(dto.getName())),
                    hasProperty("description", equalTo(dto.getDescription())),
                    hasProperty("available", equalTo(dto.getAvailable())),
                    hasProperty("requestId", equalTo(dto.getRequestId()))
            )));
        }

        //no text
        List<ItemDto> targetDtos2 = itemService.search("");
        assertThat(targetDtos2, empty());
    }

    @Test
    void createCommentWithMistake() {
        NewCommentDto newCommentDto1 = new NewCommentDto();
        newCommentDto1.setText("some text");
        assertThrows(ConditionsNotMetException.class, () -> itemService.createComment(newCommentDto1, 999L, 999L));
    }
}
