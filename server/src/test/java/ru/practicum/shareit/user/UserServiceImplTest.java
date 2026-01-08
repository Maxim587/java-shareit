package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private UserService userService;

    @Test
    void create() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        service.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserById() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = service.create(userDto);
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getId(), notNullValue());
        assertThat(userDtoById.get().getName(), equalTo(userDto.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = service.create(userDto);

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");
        updateUserDto.setEmail("newEmail@mail.com");

        userService.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(updateUserDto.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(updateUserDto.getEmail()));

        //user not exists
        assertThrows(NotFoundException.class, () -> userService.update(updateUserDto, 999L));

        //empty name
        updateUserDto.setName("");
        assertThrows(ConditionsNotMetException.class, () -> userService.update(updateUserDto, userDtoSaved.getId()));

        //empty email
        updateUserDto.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> userService.update(updateUserDto, userDtoSaved.getId()));
    }

    @Test
    void updateWithNameAndEmailIsNull() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = service.create(userDto);

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(null);
        updateUserDto.setEmail(null);

        userService.update(updateUserDto, userDtoSaved.getId());
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());

        assertThat(userDtoById.isPresent(), is(true));
        assertThat(userDtoById.get().getName(), equalTo(userDtoSaved.getName()));
        assertThat(userDtoById.get().getEmail(), equalTo(userDtoSaved.getEmail()));
    }

    @Test
    void delete() {
        UserDto userDto = makeUserDto("name", "mail@mail.com");
        UserDto userDtoSaved = service.create(userDto);

        boolean isDeleted = userService.delete(userDtoSaved.getId());
        assertThat(isDeleted, is(true));
        Optional<UserDto> userDtoById = service.getUserById(userDtoSaved.getId());
        assertThat(userDtoById.isPresent(), is(false));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}
