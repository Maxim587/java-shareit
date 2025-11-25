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

        updateUserFields(user, userDto);

        return mapper.mapToUserDto(userStorage.update(user));
    }

    @Override
    public boolean delete(long id) {
        return userStorage.delete(id);
    }

    private void updateUserFields(User currentUser, UpdateUserDto newUser) {
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            currentUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            if (!userStorage.isUniqueEmail(newUser.getEmail())) {
                throw new AlreadyExistsException("Ошибка обновления пользователя. Указанный email уже используется: " + newUser.getEmail());
            }
            currentUser.setEmail(newUser.getEmail());
        }
    }
}
