package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, NewItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return mapper.mapToItemRequestDto(requestRepository.save(mapper.mapToItemRequest(requestDto, user)));
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = requestRepository.findByRequestor_Id(userId, page);
        return requests.stream()
                .map(mapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestShortDto> getItemRequests(long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return requestRepository.findByRequestor_IdNot(userId, page).stream()
                .map(mapper::mapToItemRequestShortDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(long requestId) {
        return mapper.mapToItemRequestDto(requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id=" + requestId + " не найден")));
    }
}
