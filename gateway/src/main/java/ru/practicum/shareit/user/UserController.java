package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @Valid @RequestBody UpdateUserDto userDto) {
        log.info("Updating user with userId {}, updated user={}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Deleting user with userId={}", userId);
        return userClient.delete(userId);
    }
}
