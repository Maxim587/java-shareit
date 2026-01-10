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
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.*;

@Transactional()
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    UserDto booker;
    UserDto itemOwner;
    ItemDto item;
    NewBookingDto newBookingDto;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booker = userService.create(makeRandomUserDto());
        itemOwner = userService.create(makeRandomUserDto());
        item = itemService.create(makeItemDto(null, "name", "desc", true, null), itemOwner.getId());
        newBookingDto = makeNewBookingDto(item.getId(), NOW, BOOKING_END_DATE_IN_FUTURE);
        bookingDto = bookingService.create(newBookingDto, booker.getId());
    }

    @Test
    void createBookingShouldCreateBooking() {
        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(bookingDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(newBookingDto.getEnd()));
    }

    @Test
    void createBookingWhenNonexistentBookerShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.create(newBookingDto, 999L));
    }

    @Test
    void createBookingWhenNonexistentItemShouldThrowNotFoundException() {
        newBookingDto.setItemId(NONEXISTENT_ID);
        assertThrows(NotFoundException.class, () -> bookingService.create(newBookingDto, booker.getId()));
    }

    @Test
    void createBookingWhenBookingForItemIsApprovedShouldThrowConditionsNotMetException() {
        bookingService.approveBookingRequest(bookingDto.getId(), true, itemOwner.getId());
        NewBookingDto newBookingDto2 = makeNewBookingDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(ConditionsNotMetException.class, () -> bookingService.create(newBookingDto2, booker.getId()));
    }

    @Test
    void createBookingWhenBookingEndIsBeforeStartShouldThrowConditionsNotMetException() {
        NewBookingDto newBookingDto3 = makeNewBookingDto(item.getId(), bookingDto.getEnd(), bookingDto.getStart());
        assertThrows(ConditionsNotMetException.class, () -> bookingService.create(newBookingDto3, booker.getId()));
    }

    @Test
    void createBookingWhenBookerIsItemOwnerShouldThrowForbiddenOperationException() {
        NewBookingDto newBookingDto4 = makeNewBookingDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(ForbiddenOperationException.class, () -> bookingService.create(newBookingDto4, itemOwner.getId()));
    }

    @Test
    void findBookingByIdWhenCorrectBookingIdShouldReturnBooking() {
        BookingDto targetBookingDto = bookingService.findBookingById(bookingDto.getId(), booker.getId());

        assertThat(targetBookingDto.getId(), equalTo(bookingDto.getId()));
        assertThat(targetBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(targetBookingDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(targetBookingDto.getStatus(), equalTo(BookingStatus.WAITING.toString()));
        assertThat(targetBookingDto.getStart(), equalTo(newBookingDto.getStart()));
        assertThat(targetBookingDto.getEnd(), equalTo(newBookingDto.getEnd()));
    }

    @Test
    void findBookingByIdWhenNonexistentBookingIdShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(NONEXISTENT_ID, booker.getId()));
    }


    @Test
    void findBookingByIdWhenUserIsNotBookingCreatorShouldThrowForbiddenOperationException() {
        UserDto user2 = userService.create(makeRandomUserDto());

        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.findBookingById(bookingDto.getId(), user2.getId());
        });
    }

    @Test
    void findBookingByIdWhenUserIsItemOwnerShouldReturnBooking() {
        BookingDto targetBookingDto1 = bookingService.findBookingById(bookingDto.getId(), itemOwner.getId());
        assertThat(targetBookingDto1.getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void approveBookingWhenNonexistentBookingThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBookingRequest(NONEXISTENT_ID, true, itemOwner.getId());
        });
    }

    @Test
    void approveBookingWhenUserIsNotItemOwnerShouldThrowForbiddenOperationException() {
        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBookingRequest(bookingDto.getId(), true, booker.getId());
        });
    }

    @Test
    void approveBookingWhenApproveWithTrueShouldChangeBookingStatusToApproved() {
        BookingDto bookingDto1 = bookingService.approveBookingRequest(bookingDto.getId(), true, itemOwner.getId());
        assertThat(bookingDto1.getStatus(), equalTo(BookingStatus.APPROVED.toString()));
    }

    @Test
    void approveBookingWhenBookingStatusIsApprovedShouldThrowForbiddenOperationException() {
        bookingService.approveBookingRequest(bookingDto.getId(), true, itemOwner.getId());
        assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBookingRequest(bookingDto.getId(), true, itemOwner.getId());
        });
    }

    @Test
    void findUserBookingsWhenStateIsAllShouldReturnAllUserBookings() {
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsWhenStateIsWaitingShouldReturnUserBookingsWithWaitingStatus() {
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsWhenStateIsPastShouldReturnPastUserBookings() {
        newBookingDto.setStart(BOOKING_START_DATE_IN_PAST);
        newBookingDto.setEnd(BOOKING_END_DATE_IN_PAST);
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsWhenStateIsFutureShouldReturnFutureUserBookings() {
        newBookingDto.setStart(BOOKING_START_DATE_IN_FUTURE);
        newBookingDto.setEnd(BOOKING_END_DATE_IN_FUTURE);
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsWhenStateIsCurrentShouldReturnCurrentUserBookings() {
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserBookingsWhenStateIsRejectedShouldReturnUserBookingsWithRejectedStatus() {
        bookingDto = bookingService.approveBookingRequest(bookingDto.getId(), false, itemOwner.getId());
        List<BookingDto> dtos = bookingService.findUserBookings(booker.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsWhenStateIsAllShouldReturnAllUserItemBookings() {
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }


    @Test
    void findUserItemBookingsWhenStateIsWaitingShouldReturnUserItemBookingsWithWaitingStatus() {
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsWhenStateIsCurrentShouldReturnCurrentUserItemBookings() {
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsWhenStateIsPastShouldReturnPastUserItemBookings() {
        newBookingDto.setStart(LocalDateTime.now().minusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().minusDays(8L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsWhenStateIsFutureShouldReturnFutureUserItemBookings() {
        newBookingDto.setStart(LocalDateTime.now().plusDays(10L));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(88L));
        bookingDto = bookingService.create(newBookingDto, booker.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void findUserItemBookingsWhenStateIsRejectedShouldReturnUserItemBookingsWithRejectedStatus() {
        bookingService.approveBookingRequest(bookingDto.getId(), false, itemOwner.getId());
        List<BookingDto> dtos = bookingService.findUserItemsBookings(itemOwner.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(dtos.size(), equalTo(1));
        assertThat(dtos.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void bookingStateOfWhenPassedNonexistentValueThenShouldThrowConditionsNotMetException() {
        assertThrows(ConditionsNotMetException.class, () -> BookingState.of("NonexistentValue"));
    }
}
