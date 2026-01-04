package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDto create(NewBookingDto bookingDto, Long bookerId) {
        checkDates(bookingDto.getStart(), bookingDto.getEnd());
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Ошибка бронирования предмета. Пользователь с id " + bookerId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Ошибка бронирования предмета. Предмет с id " + bookingDto.getItemId() + " не найден"));

        checkBookerIsNotOwner(bookerId, item);
        checkAvailable(item, bookingDto);
        Booking booking = mapper.mapToBooking(bookingDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBookingRequest(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Ошибка подтверждения бронирования предмета. " +
                                                         "Бронирование с id " + bookingId + " не найдено"));
        checkUserIsItemOwner(booking, userId);
        checkStatus(booking.getStatus());
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        checkUserPermissions(booking, userId);
        return mapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findUserBookings(Long bookerId, String state) {
        Iterable<Booking> bookings = switch (BookingState.of(state)) {
            case ALL -> bookingRepository.findByBooker_IdOrderByStartDesc(bookerId);
            case PAST -> bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBooker_idAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case FUTURE ->
                    bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findByBooker_IdAndStartAfterAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case REJECTED ->
                    bookingRepository.findByBooker_idAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
        };

        return mapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findUserItemsBookings(Long ownerId, String state) {
        checkUserOwnsAnyItem(ownerId);

        Iterable<Booking> bookings = switch (BookingState.of(state)) {
            case ALL -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
            case PAST ->
                    bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case WAITING ->
                    bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case FUTURE ->
                    bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findByItem_Owner_IdAndStartAfterAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case REJECTED ->
                    bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        return mapper.mapToBookingDtoList(bookings);
    }

    private boolean checkUserIsItemOwner(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenOperationException("Пользователь с id " + userId + " не является владельцем предмета");
        }
        return true;
    }

    private void checkUserPermissions(Booking booking, Long userId) {
        if (!(booking.getBooker().getId().equals(userId) ||
              checkUserIsItemOwner(booking, userId))) {
            throw new ForbiddenOperationException("Пользователь с id " + userId + " не является автором бронирования");
        }
    }

    private void checkUserOwnsAnyItem(Long userId) {
        if (!itemRepository.existsItemByOwner_Id(userId)) {
            throw new NotFoundException("У пользователя с id " + userId + " нет предметов, которыми он владеет");
        }
    }

    private void checkStatus(BookingStatus status) {
        if (status != BookingStatus.WAITING) {
            throw new ForbiddenOperationException("Операция недопустима для текущего статуса бронирования");
        }
    }

    private void checkAvailable(Item item, NewBookingDto bookingDto) {
        if (!item.isAvailable() || checkIntersectionByDates(item.getId(), bookingDto)) {
            throw new ConditionsNotMetException("Предмет недоступен для бронирования");
        }
    }

    private boolean checkIntersectionByDates(Long itemId, NewBookingDto bookingDto) {
        return bookingRepository.findByItem_IdAndStatusAndEndAfter(itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .stream().anyMatch(booking ->
                        !(bookingDto.getEnd().isBefore(booking.getStart()) ||
                          bookingDto.getStart().isAfter(booking.getEnd()))
                );
    }

    private void checkDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate)) {
            throw new ConditionsNotMetException("Дата окончания бронирования не может быть раньше даты начала");
        }
    }

    private void checkBookerIsNotOwner(Long bookerId, Item item) {
        if (bookerId.equals(item.getOwner().getId())) {
            throw new ForbiddenOperationException("Ошибка бронирования предмета. Пользователь id:" + bookerId + " является владельцем предмета");
        }
    }
}
