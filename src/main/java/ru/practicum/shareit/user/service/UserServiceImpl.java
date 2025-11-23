package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public Optional<UserDto> getUserById(long id) {
        return userStorage.getUserById(id).map(mapper::mapToUserDto);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (!userStorage.isUniqueEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("Ошибка создания пользователя. Указанный email уже используется: " + userDto.getEmail());
        }

        return mapper.mapToUserDto(userStorage.create(mapper.mapToUser(userDto)));
    }

    @Override
    public UserDto update(UpdateUserDto userDto, long id) {

        User user = userStorage.getUserById(id).orElseThrow(() ->
                new NotFoundException("Ошибка обновления пользователя. Пользователь не найден id:" + id));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!userStorage.isUniqueEmail(userDto.getEmail())) {
                throw new AlreadyExistsException("Ошибка обновления пользователя. Указанный email уже используется: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }
        return mapper.mapToUserDto(userStorage.update(user));
    }

    @Override
    public boolean delete(long id) {
        return userStorage.delete(id);
    }
}
