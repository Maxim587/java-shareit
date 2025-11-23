package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> getUserById(long id);

    UserDto create(UserDto userDto);

    UserDto update(UpdateUserDto userDto, long id);

    boolean delete(long id);
}
