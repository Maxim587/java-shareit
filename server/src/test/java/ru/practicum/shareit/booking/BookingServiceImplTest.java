package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    UserDto booker;
    UserDto owner;
    ItemDto item;
    NewBookingDto newBookingDto;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        UserDto bookerDto = makeUserDto("name", "mail@mail.com");
        booker = userService.create(bookerDto);

        UserDto ownerDto = makeUserDto("name1", "mail1@mail.com");
        owner = userService.create(ownerDto);

        ItemDto itemDto = makeItemDto(null, "name", "desc", true, null);
        item = itemService.create(itemDto, owner.getId());

        newBookingDto = makeNewBookingDto(item.getId(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2L).truncatedTo(ChronoUnit.SECONDS)
        );
        bookingDto = bookingService.create(newBookingDto, booker.getId());
    }

    @Test
    void createBooking() {
        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(bookingDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(newBookingDto.getEnd()));

        //booker not found
        assertThrows(NotFoundException.class, () -> bookingService.create(newBookingDto, 999L));

        //item not found
        newBookingDto.setItemId(999L);
        assertThrows(NotFoundException.class, () -> bookingService.create(newBookingDto, booker.getId()));

        //item is already booked
        bookingService.approveBookingRequest(bookingDto.getId(), true, owner.getId());
        NewBookingDto newBookingDto2 = makeNewBookingDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(ConditionsNotMetException.class, () -> bookingService.create(newBookingDto2, booker.getId()));

        //wrong start and end dates
        NewBookingDto newBookingDto3 = makeNewBookingDto(item.getId(), bookingDto.getEnd(), bookingDto.getStart());
        assertThrows(ConditionsNotMetException.class, () -> bookingService.create(newBookingDto3, booker.getId()));

        //booker == owner
        NewBookingDto newBookingDto4 = makeNewBookingDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(ForbiddenOperationException.class, () -> bookingService.create(newBookingDto4, owner.getId()));
    }

    @Test
    void findBookingById() {
        BookingDto targetBookingDto = bookingService.findBookingById(bookingDto.getId(), booker.getId());

        assertThat(targetBookingDto.getId(), equalTo(bookingDto.getId()));
        assertThat(targetBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(targetBookingDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(targetBookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(targetBookingDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(targetBookingDto.getEnd(), equalTo(newBookingDto.getEnd()));

        //booking not found
        assertThrows(NotFoundException.class, () -> {
            bookingService.findBookingById(999L, booker.getId());
        });

        UserDto userDto2 = makeUserDto("name1", "ex@ex.ru");
        UserDto user2 = userService.create(userDto2);

        //user doesn't have permissions
        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.findBookingById(bookingDto.getId(), 999L);
        });

        //request from item owner
        BookingDto targetBookingDto1 = bookingService.findBookingById(bookingDto.getId(), owner.getId());
        assertThat(targetBookingDto1.getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void approveBooking() {
        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBookingRequest(bookingDto.getId(), true, booker.getId());
        });

        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBookingRequest(999L, true, booker.getId());
        });

        BookingDto approved = bookingService.approveBookingRequest(bookingDto.getId(), true, owner.getId());
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED.toString()));

        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBookingRequest(bookingDto.getId(), false, booker.getId());
        });
    }

    @Test
    void approveBookingNotWaiting() {
        BookingDto approved = bookingService.approveBookingRequest(bookingDto.getId(), true, owner.getId());
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED.toString()));

        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBookingRequest(bookingDto.getId(), true, owner.getId());
        });
    }

    @Test
    void findUserBookings() {
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));

        dtos = bookingService.findUserBookings(booker.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsPast() {
        newBookingDto.setStart(LocalDateTime.now().minusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().minusDays(8L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsFuture() {
        newBookingDto.setStart(LocalDateTime.now().plusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(88L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsCurrent() {
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsRejected() {
        bookingDto = bookingService.approveBookingRequest(bookingDto.getId(), false, owner.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookings() {
        List<BookingDto> dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));

        dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));

        assertThrows(NotFoundException.class, () -> {
            bookingService.findUserItemsBookings(booker.getId(), BookingState.CURRENT.toString(), 0, 10);
        });
    }

    @Test
    void findUserItemBookingsCurrent() {
        List<BookingDto> dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsPast() {
        newBookingDto.setStart(LocalDateTime.now().minusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().minusDays(8L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsFuture() {
        newBookingDto.setStart(LocalDateTime.now().plusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(88L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsRejected() {
        bookingService.approveBookingRequest(bookingDto.getId(), false, owner.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(owner.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void bookingStateOf() {
        assertThrows(ConditionsNotMetException.class, () -> BookingState.of("qqq"));
    }
}
