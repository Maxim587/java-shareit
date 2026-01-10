package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Test
    void createShouldSaveBookingInDatabase() {
        User itemOwner = new User(null, "name", "mail@mail.ru");
        User booker = new User(null, "name2", "mail@mail.com");
        userRepository.save(itemOwner);
        userRepository.save(booker);

        Item item = new Item(null, "name", "desc1", true, itemOwner, null);
        itemRepository.save(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS),
                item,
                booker,
                BookingStatus.APPROVED
        );
        bookingRepository.save(booking);

        Booking bookingdb = bookingRepository.findAll().getFirst();

        assertNotNull(bookingdb);
        assertNotNull(bookingdb.getId());
        assertEquals(booking.getId(), bookingdb.getId());
        assertEquals(booking.getStart(), bookingdb.getStart());
        assertEquals(booking.getEnd(), bookingdb.getEnd());
        assertEquals(booking.getItem(), bookingdb.getItem());
        assertEquals(booking.getBooker(), bookingdb.getBooker());
    }
}
