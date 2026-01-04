package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public Collection<ItemExtendedDto> getUserItems(Long userId) {
        List<ItemExtendedDto> items = itemMapper.mapToItemExtendedDtoList(itemRepository.getItemsByOwner_Id(userId));
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ItemExtendedDto> itemsMap = items.stream().collect(Collectors.toMap(ItemExtendedDto::getId, Function.identity()));
        setLastBookings(itemsMap);
        setNextBookings(itemsMap);
        setComments(itemsMap);

        return itemsMap.values();
    }

    @Override
    public ItemExtendedDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет не найден id:" + itemId));

        Map<Long, ItemExtendedDto> itemsMap = Map.of(itemId, itemMapper.mapToItemExtendedDto(item));
        if (item.getOwner().getId().equals(userId)) {
            setLastBookings(itemsMap);
            setNextBookings(itemsMap);
        }
        setComments(itemsMap);
        return itemsMap.get(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemMapper.mapToItemDtoList(itemRepository.search(text));
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long owner) {
        User user = userRepository.findById(owner)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + owner + " не найден"));
        Item item = itemMapper.mapToItem(itemDto, user);
        return itemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(UpdateItemDto itemDto, Long itemId, Long owner) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Ошибка обновления предмета. Предмет не найден id:" + itemId));

        updateItemFields(item, itemDto, owner);

        return itemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto commentDto, Long itemId, Long userId) {
        if (!bookingRepository.existsByItem_IdAndBooker_idAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ConditionsNotMetException("Ошибка добавления комментария к предмету id " + itemId +
                                                ". У пользователя с id " + userId + " нет завершенных бронирований для предмета");
        }

        User user = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Ошибка добавления комментария к предмету. Предмет не найден id:" + itemId));

        return commentMapper.mapToCommentDto(commentRepository.save(new Comment(null, commentDto.getText(), user, item)));
    }

    private void updateItemFields(Item currentItem, UpdateItemDto newItem, Long owner) {
        if (!currentItem.getOwner().getId().equals(owner)) {
            throw new ForbiddenOperationException("Ошибка обновления предмета id:" + currentItem.getId() +
                                                  ". Пользователь с id:" + owner + " не является владельцем предмета");
        }

        if (newItem.getName() != null) {
            if (newItem.getName().isBlank()) {
                throw new ConditionsNotMetException("Ошибка обновления предмета id:" + currentItem.getId() +
                                                    ". Название не может быть пустым");
            }
            currentItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            currentItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            currentItem.setAvailable(newItem.getAvailable());
        }
    }

    private void setLastBookings(Map<Long, ItemExtendedDto> itemsMap) {
        bookingRepository.findAllByItem_IdInAndStatusAndEndBeforeOrderByIdAscEndDesc(
                        itemsMap.keySet(),
                        BookingStatus.APPROVED,
                        LocalDateTime.now())
                .forEach(booking -> {
                    ItemExtendedDto itemExtendedDto = itemsMap.get(booking.getItem().getId());
                    if (itemExtendedDto.getLastBooking() == null) {
                        itemExtendedDto.setLastBooking(bookingMapper.mapToBookingDatesDto(booking));
                    }
                });
    }

    private void setNextBookings(Map<Long, ItemExtendedDto> itemsMap) {
        bookingRepository.findAllByItem_IdInAndStatusAndStartGreaterThanEqualOrderByIdAscStartAsc(
                        itemsMap.keySet(),
                        BookingStatus.APPROVED,
                        LocalDateTime.now())
                .forEach(booking -> {
                    ItemExtendedDto itemExtendedDto = itemsMap.get(booking.getItem().getId());
                    if (itemExtendedDto.getNextBooking() == null) {
                        itemExtendedDto.setNextBooking(bookingMapper.mapToBookingDatesDto(booking));
                    }
                });
    }

    private void setComments(Map<Long, ItemExtendedDto> itemsMap) {
        commentRepository.findAllByItem_IdIn(itemsMap.keySet())
                .forEach(comment -> {
                    itemsMap.get(comment.getItem().getId()).getComments().add(commentMapper.mapToCommentDto(comment));
                });
    }
}
