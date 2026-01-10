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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Util.NONEXISTENT_ID;

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
    void createShouldCreateUser() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(userDto.getName())),
                hasProperty("email", equalTo(userDto.getEmail()))
        ));
    }

    @Test
    void getUserByIdWhenCorrectIdShouldReturnUser() {
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getId(), is(userDtoSaved.getId()));
        assertThat(userDtoById.get().getName(), is(userDto.getName()));
        assertThat(userDtoById.get().getEmail(), is(userDto.getEmail()));
    }

    @Test
    void updateShouldUpdateUser() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");
        updateUserDto.setEmail("newEmail@mail.com");

        service.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(updateUserDto.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void updateWhenUserNotExistsShouldThrowNotFoundException() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");
        updateUserDto.setEmail("newEmail@mail.com");

        assertThrows(NotFoundException.class, () -> service.update(updateUserDto, NONEXISTENT_ID));
    }

    @Test
    void updateWhenEmptyNameThenThrowConditionsNotMetException() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("");

        assertThrows(ConditionsNotMetException.class, () -> service.update(updateUserDto, userDtoSaved.getId()));
    }

    @Test
    void updateWhenEmptyEmailThenThrowConditionsNotMetException() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("");

        assertThrows(ConditionsNotMetException.class, () -> service.update(updateUserDto, userDtoSaved.getId()));
    }


    @Test
    void updateWhenNameIsNullThenUserNameShouldNotBeUpdated() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        service.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(userDtoSaved.getName()));
    }

    @Test
    void updateWhenEmailIsNullThenUserEmailShouldNotBeUpdated() {
        UpdateUserDto updateUserDto = new UpdateUserDto();

        service.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getEmail(), equalTo(userDtoSaved.getEmail()));
    }

    @Test
    void deleteShouldDeleteUser() {
        boolean isDeleted = service.delete(userDtoSaved.getId());
        assertThat(isDeleted, is(true));
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());
        assertThat(userDtoById.isPresent(), is(false));
    }
}
