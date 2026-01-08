package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Test
    void create() {
        User user = new User(null, "name", "mail@mail.ru");
        userRepository.save(user);

        ItemRequest request = new ItemRequest(null, "desc", user, null);
        ItemRequest requestdb = itemRequestRepository.save(request);

        assertNotNull(requestdb);
        assertNotNull(requestdb.getId());
        assertEquals(requestdb.getRequestor().getId(), user.getId());
        assertEquals(requestdb.getDescription(), request.getDescription());
    }
}
