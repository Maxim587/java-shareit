package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_idAndEndBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByBooker_Id(Long bookerId, Pageable page);

    List<Booking> findByBooker_idAndStatus(Long bookerId, BookingStatus bookingStatus, Pageable page);

    List<Booking> findByBooker_IdAndStartAfter(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByItem_Owner_Id(Long itemOwnerId, Pageable page);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long itemOwnerId, LocalDateTime end, Pageable page);

    List<Booking> findByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus bookingStatus, Pageable page);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long itemOwnerId, LocalDateTime start, Pageable page);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long itemOwnerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findAllByItem_IdInAndStatusAndEndBeforeOrderByIdAscEndDesc(Collection<Long> itemIds, BookingStatus bookingStatus, LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStatusAndStartGreaterThanEqualOrderByIdAscStartAsc(Collection<Long> itemIds, BookingStatus bookingStatus, LocalDateTime start);

    boolean existsByItem_IdAndBooker_idAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus bookingStatus, LocalDateTime end);

    List<Booking> findByItem_IdAndStatusAndEndAfter(Long itemId, BookingStatus bookingStatus, LocalDateTime end);
}
