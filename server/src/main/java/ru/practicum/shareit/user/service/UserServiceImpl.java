package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(mapper::mapToUserDto);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return mapper.mapToUserDto(userRepository.save(mapper.mapToUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserDto userDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Ошибка обновления пользователя. Пользователь не найден id:" + id));

        updateUserFields(user, userDto);

        return mapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        userRepository.deleteById(id);
        return true;
    }

    private void updateUserFields(User currentUser, UpdateUserDto newUser) {
        if (newUser.getName() != null) {
            if (newUser.getName().isBlank()) {
                throw new ConditionsNotMetException("Имя пользователя не может быть пустым");
            }
            currentUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Email пользователя не может быть пустым");
            }
            currentUser.setEmail(newUser.getEmail());
        }
    }
}
