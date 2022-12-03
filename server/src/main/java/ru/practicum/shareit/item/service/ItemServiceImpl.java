package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.item.ItemForbiddenException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.CommentBadRequestException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.item.data.Comment;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.item.data.dto.CommentDto;
import ru.practicum.shareit.item.data.dto.CommentRequestDto;
import ru.practicum.shareit.item.data.dto.ItemDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;
import ru.practicum.shareit.item.data.mapper.CommentMapper;
import ru.practicum.shareit.item.data.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto create(long userId, StandardItemDto itemDto) {
        checkIfUserExists(userId);
        Item item = ItemMapper.fromStandardItemDto(itemDto);
        item.setOwnerId(userId);
        setRequest(item, itemDto.getRequestId());
        return ItemMapper.toStandardItemDto(itemRepository.save(item), null);
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, long userId, StandardItemDto itemDto) {
        checkIfItemExists(itemId);
        Item updatedItem = itemRepository.getReferenceById(itemId);
        checkItemOwner(userId, updatedItem.getOwnerId());
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        setRequest(updatedItem, itemDto.getRequestId());
        List<CommentDto> comments = commentRepository
                .findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toStandardItemDto(updatedItem, comments);
    }

    @Override
    public ItemDto get(long itemId, long ownerId) {
        checkIfItemExists(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        Optional<Booking> lastBooking = bookingRepository
                .getLastByItemId(itemId, ownerId, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .findFirst();
        Optional<Booking> nextBooking = bookingRepository
                .getNextByItemId(itemId, ownerId, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .findFirst();
        List<CommentDto> comments = commentRepository
                .findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (lastBooking.isEmpty() && nextBooking.isEmpty()) {
            return ItemMapper.toStandardItemDto(item, comments);
        } else {
            return ItemMapper.toWithBookingItemDto(item, comments, lastBooking, nextBooking);
        }
    }

    @Override
    public List<ItemDto> get(long ownerId) {
        checkIfUserExists(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        Map<Long, List<CommentDto>> commentsMap = getAllCommentsForItemIdByOwnerId(ownerId);
        Map<Long, List<Booking>> bookingsMap = getAllBookingsForItemIdByOwnerId(ownerId);
        return items
                .stream()
                .map(item -> {
                    long itemId = item.getId();
                    if (bookingsMap.get(itemId) != null) {
                        Optional<Booking> lastBooking = getLastBookingByItemId(bookingsMap.get(item.getId()));
                        Optional<Booking> nextBooking = getNextBookingByItemId(bookingsMap.get(item.getId()));
                        if (lastBooking.isPresent() && nextBooking.isPresent()) {
                            return ItemMapper.toWithBookingItemDto(item, commentsMap.get(item.getId()), lastBooking, nextBooking);
                        }
                    }
                    return ItemMapper.toStandardItemDto(item, commentsMap.get(item.getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository
                .findAllByText(text)
                .stream()
                .map((Function<Item, ItemDto>) item -> ItemMapper.toStandardItemDto(item, null))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long itemId, long userId, CommentRequestDto commentRequestDto) {
        checkIfUserExists(userId);
        checkIfItemExists(itemId);
        checkIfBookingsWithItemExist(itemId);
        User user = userRepository.getReferenceById(userId);
        Item item = itemRepository.getReferenceById(itemId);
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void checkIfUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    private void checkIfItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не найдена.", itemId));
        }
    }

    private void checkItemOwner(long userId, long ownerId) {
        if (userId != ownerId) {
            throw new ItemForbiddenException("Указан неверный владелец вещи.");
        }
    }

    private void checkIfBookingsWithItemExist(long itemId) {
        if (bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.REJECTED).isEmpty()) {
            throw new CommentBadRequestException(String.format("Нельзя добавить комментарий к вещи %d без бронирования.", itemId));
        }
        if (bookingRepository.findAllCurrentByItemId(itemId).isEmpty()) {
            throw new CommentBadRequestException(String.format("Нельзя добавить комментарий к вещи %d с запланированными бронированиями.", itemId));
        }
    }

    private Map<Long, List<CommentDto>> getAllCommentsForItemIdByOwnerId(long ownerId) {
        Map<Long, List<CommentDto>> map = new LinkedHashMap<>();
        commentRepository.findAllByOwnerId(ownerId).forEach(comment -> {
            long itemId = comment.getItem().getId();
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            if (map.containsKey(itemId)) {
                map.get(itemId).add(commentDto);
            } else {
                map.put(itemId, List.of(commentDto));
            }
        });
        return map;
    }

    private Map<Long, List<Booking>> getAllBookingsForItemIdByOwnerId(long ownerId) {
        Map<Long, List<Booking>> bookings = new LinkedHashMap<>();
        bookingRepository
                .findAllByOwner(ownerId, Sort.by(Sort.Direction.ASC, "start"))
                .forEach(booking -> {
                    long itemId = booking.getItem().getId();
                    if (bookings.containsKey(itemId)) {
                        bookings.get(itemId).add(booking);
                    } else {
                        List<Booking> list = new LinkedList<>();
                        list.add(booking);
                        bookings.put(itemId, list);
                    }
                });
        return bookings;
    }

    private Optional<Booking> getLastBookingByItemId(List<Booking> bookings) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getStart().isAfter(LocalDateTime.now()) && i != 0) {
                return Optional.of(bookings.get(i - 1));
            }
        }
        return Optional.empty();
    }

    private Optional<Booking> getNextBookingByItemId(List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

    private void setRequest(Item item, Long requestId) {
        if (requestId != null && itemRequestRepository.existsById(requestId)) {
            item.setRequest(itemRequestRepository.getReferenceById(requestId));
        }
    }
}