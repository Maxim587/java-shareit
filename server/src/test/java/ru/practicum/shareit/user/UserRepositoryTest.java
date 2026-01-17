package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    void createShouldSaveUserInDatabase() {
        final User user = new User();
        user.setName("name");
        user.setEmail("email");
        User user1 = userRepository.save(user);
        assertNotNull(user1);
        assertNotNull(user1.getId());
        assertThat(user1.getName()).isEqualTo(user.getName());
        assertThat(user1.getEmail()).isEqualTo(user.getEmail());
    }
}

