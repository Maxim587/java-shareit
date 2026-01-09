package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    UserDto userDto;
    UserDto userDtoSaved;

    @BeforeEach
    void setUp() {
        userDto = Util.makeUserDto("name", "mail@mail.com");
        userDtoSaved = service.create(userDto);
    }

    @Test
    void create() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserById() {
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getId(), notNullValue());
        assertThat(userDtoById.get().getName(), equalTo(userDto.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");
        updateUserDto.setEmail("newEmail@mail.com");

        service.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(updateUserDto.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(updateUserDto.getEmail()));

        //user not exists
        assertThrows(NotFoundException.class, () -> service.update(updateUserDto, 999L));

        //empty name
        updateUserDto.setName("");
        assertThrows(ConditionsNotMetException.class, () -> service.update(updateUserDto, userDtoSaved.getId()));

        //empty email
        updateUserDto.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> service.update(updateUserDto, userDtoSaved.getId()));
    }

    @Test
    void updateWithNameAndEmailAreNull() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(null);
        updateUserDto.setEmail(null);

        service.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(userDtoSaved.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(userDtoSaved.getEmail()));
    }

    @Test
    void delete() {
        boolean isDeleted = service.delete(userDtoSaved.getId());
        assertThat(isDeleted, is(true));
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());
        assertThat(userDtoById.isPresent(), is(false));
    }
}
