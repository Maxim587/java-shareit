package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Iterable<Booking> findByBooker_idAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    Iterable<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    Iterable<Booking> findByBooker_idAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    Iterable<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    Iterable<Booking> findByBooker_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    Iterable<Booking> findByItem_Owner_IdOrderByStartDesc(Long itemOwnerId);

    Iterable<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long itemOwnerId, LocalDateTime end);

    Iterable<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long itemOwnerId, BookingStatus bookingStatus);

    Iterable<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start);

    Iterable<Booking> findByItem_Owner_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long itemOwnerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStatusAndEndBeforeOrderByIdAscEndDesc(Collection<Long> itemIds, BookingStatus bookingStatus, LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStatusAndStartGreaterThanEqualOrderByIdAscStartAsc(Collection<Long> itemIds, BookingStatus bookingStatus, LocalDateTime start);

    boolean existsByItem_IdAndBooker_idAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus bookingStatus, LocalDateTime end);

}
