package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid NewItemRequestDto requestDto) {
        log.info("Creating item request {}, userId={}", requestDto, userId);
        return itemRequestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get item requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getUserItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get item requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable long requestId) {
        log.info("Get item request with requestId={}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }
}
