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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


    @Test
    void getUserItemsShouldReturnAllItemsOwnedByCurrentUser() {
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

        assertThat(targetDtos, hasSize(savedDtos.size()));
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
    void getItemByIdWhenCorrectItemIdShouldReturnItem() {
        UserDto booker = userService.create(makeRandomUserDto());
        UserDto itemOwner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), itemOwner.getId());

        NewBookingDto newBookingDto1 = new NewBookingDto();
        newBookingDto1.setItemId(item.getId());
        newBookingDto1.setStart(BOOKING_START_DATE_IN_PAST);
        newBookingDto1.setEnd(BOOKING_END_DATE_IN_PAST);
        BookingDto lastBooking = bookingService.create(newBookingDto1, booker.getId());
        bookingService.approveBookingRequest(lastBooking.getId(), true, itemOwner.getId());

        NewBookingDto newBookingDto2 = new NewBookingDto();
        newBookingDto2.setItemId(item.getId());
        newBookingDto2.setStart(BOOKING_START_DATE_IN_FUTURE);
        newBookingDto2.setEnd(BOOKING_END_DATE_IN_FUTURE);
        BookingDto nextBooking = bookingService.create(newBookingDto2, booker.getId());
        bookingService.approveBookingRequest(nextBooking.getId(), true, itemOwner.getId());

        NewCommentDto newCommentDto1 = new NewCommentDto();
        NewCommentDto newCommentDto2 = new NewCommentDto();
        newCommentDto1.setText("some text");
        newCommentDto2.setText("some text2");
        CommentDto comment1 = itemService.createComment(newCommentDto1, item.getId(), booker.getId());
        CommentDto comment2 = itemService.createComment(newCommentDto2, item.getId(), booker.getId());
        List<CommentDto> sourceComments = Arrays.asList(comment1, comment2);

        ItemExtendedDto itemExtendedDto = itemService.getItemById(item.getId(), itemOwner.getId());
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

        List<CommentDto> targetComments = itemExtendedDto.getComments();

        assertThat(targetComments, hasSize(sourceComments.size()));
        for (CommentDto sourceComment : sourceComments) {
            assertThat(targetComments, hasItem(allOf(
                    hasProperty("id", equalTo(sourceComment.getId())),
                    hasProperty("text", equalTo(sourceComment.getText())),
                    hasProperty("itemId", equalTo(sourceComment.getItemId())),
                    hasProperty("authorName", equalTo(sourceComment.getAuthorName())),
                    hasProperty("created", equalTo(sourceComment.getCreated()))
            )));
        }
    }

    @Test
    void getItemByIdWhenNonexistentItemIdShouldThrowNotFoundException() {
        UserDto owner = userService.create(makeRandomUserDto());
        assertThrows(NotFoundException.class, () -> itemService.getItemById(NONEXISTENT_ID, owner.getId()));
    }

    @Test
    void updateShouldUpdateItem() {
        UserDto owner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("New name");
        updateItemDto.setDescription("New description");
        updateItemDto.setAvailable(true);

        ItemDto updatedItem = itemService.update(updateItemDto, item.getId(), owner.getId());

        assertThat(updatedItem.getId(), equalTo(item.getId()));
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
    }

    @Test
    void updateWhenUserIsNotOwnerShouldThrowForbiddenOperationException() {
        UserDto owner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());
        UserDto notOwner = userService.create(makeRandomUserDto());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("New name");
        updateItemDto.setDescription("New description");
        updateItemDto.setAvailable(true);

        assertThrows(ForbiddenOperationException.class, () -> itemService.update(updateItemDto, item.getId(), notOwner.getId()));
    }

    @Test
    void updateWhenEmptyNameShouldThrowConditionsNotMetException() {
        UserDto owner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("");
        updateItemDto.setDescription("New description");
        updateItemDto.setAvailable(true);

        assertThrows(ConditionsNotMetException.class, () -> itemService.update(updateItemDto, item.getId(), owner.getId()));
    }

    @Test
    void updateWhenNonexistentItemIdShouldThrowNotFoundException() {
        UserDto owner = userService.create(makeRandomUserDto());
        itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("New Name");
        updateItemDto.setDescription("New description");
        updateItemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.update(updateItemDto, NONEXISTENT_ID, owner.getId()));
    }


    @Test
    void updateWhenNewValueIsNullShouldNotUpdate() {
        UserDto owner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());

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
    void searchWhenTextNotEmptyShouldReturnItemsWithMatchingNameOrDescription() {
        UserDto owner = userService.create(makeRandomUserDto());

        ItemDto itemDto1 = makeItemDto(null, "name with keyText for being searched", "desc1", true, null);
        ItemDto item1 = itemService.create(itemDto1, owner.getId());

        ItemDto itemDto2 = makeItemDto(null, "name", "description with keyText for being searched", true, null);
        ItemDto item2 = itemService.create(itemDto2, owner.getId());

        List<ItemDto> savedDtos = Arrays.asList(item1, item2);
        List<ItemDto> targetDtos = itemService.search("keyText");

        assertThat(targetDtos, hasSize(savedDtos.size()));
        for (ItemDto dto : savedDtos) {
            assertThat(targetDtos, hasItem(allOf(
                    hasProperty("id", equalTo(dto.getId())),
                    hasProperty("name", equalTo(dto.getName())),
                    hasProperty("description", equalTo(dto.getDescription())),
                    hasProperty("available", equalTo(dto.getAvailable())),
                    hasProperty("requestId", equalTo(dto.getRequestId()))
            )));
        }
    }

    @Test
    void searchWhenTextIsEmptyShouldReturnNoItems() {
        UserDto owner = userService.create(makeRandomUserDto());

        ItemDto itemDto1 = makeItemDto(null, "name with keyText for being searched", "desc1", true, null);
        itemService.create(itemDto1, owner.getId());

        ItemDto itemDto2 = makeItemDto(null, "1234", "description with keyText for being searched", true, null);
        itemService.create(itemDto2, owner.getId());

        List<ItemDto> targetDtos = itemService.search("");

        assertThat(targetDtos, empty());
    }

    @Test
    void createCommentWhenNonexistentItemShouldThrowConditionsNotMetException() {
        UserDto owner = userService.create(makeRandomUserDto());
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("some text");
        assertThrows(ConditionsNotMetException.class, () -> itemService.createComment(newCommentDto, NONEXISTENT_ID, owner.getId()));
    }

    @Test
    void createCommentWhenNonexistentUserShouldThrowConditionsNotMetException() {
        UserDto owner = userService.create(makeRandomUserDto());
        ItemDto item = itemService.create(makeItemDto(null, "name1", "desc1", true, null), owner.getId());

        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("some text");
        assertThrows(ConditionsNotMetException.class, () -> itemService.createComment(newCommentDto, item.getId(), NONEXISTENT_ID));
    }

}
