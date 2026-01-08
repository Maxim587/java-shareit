package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;


    @Test
    void createBooking() {
        UserDto bookerDto = makeUserDto("name", "mail@mail.com");
        Long bookerId = userService.create(bookerDto).getId();

        UserDto ownerDto = makeUserDto("name1", "mail1@mail.com");
        Long ownerId = userService.create(ownerDto).getId();

        ItemDto itemDto = makeItemDto(null, "name", "desc", true, null);
        Long savedItemId = itemService.create(itemDto, ownerId).getId();

        NewBookingDto newBookingDto = makeNewBookingDto(savedItemId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS)
        );
        BookingDto bookingDtoSaved = bookingService.create(newBookingDto, bookerId);

        assertThat(bookingDtoSaved.getId(), notNullValue());
        assertThat(bookingDtoSaved.getItem().getId(), equalTo(savedItemId));
        assertThat(bookingDtoSaved.getBooker().getId(), equalTo(bookerId));
        assertThat(bookingDtoSaved.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(bookingDtoSaved.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(bookingDtoSaved.getEnd(), equalTo(newBookingDto.getEnd()));
    }

    @Test
    void findBookingById() {
        UserDto bookerDto = makeUserDto("name", "mail@mail.com");
        Long bookerId = userService.create(bookerDto).getId();

        UserDto ownerDto = makeUserDto("name1", "mail1@mail.com");
        Long ownerId = userService.create(ownerDto).getId();

        ItemDto itemDto = makeItemDto(null, "name", "desc", true, null);
        Long savedItemId = itemService.create(itemDto, ownerId).getId();

        NewBookingDto newBookingDto = makeNewBookingDto(savedItemId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS)
        );
        Long savedBookingId = bookingService.create(newBookingDto, bookerId).getId();

        BookingDto targetBookingDto = bookingService.findBookingById(savedBookingId, bookerId);

        assertThat(targetBookingDto.getId(), equalTo(savedBookingId));
        assertThat(targetBookingDto.getItem().getId(), equalTo(savedItemId));
        assertThat(targetBookingDto.getBooker().getId(), equalTo(bookerId));
        assertThat(targetBookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(targetBookingDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(targetBookingDto.getEnd(), equalTo(newBookingDto.getEnd()));
    }

    private NewBookingDto makeNewBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
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
