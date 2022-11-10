package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentBadRequestException;
import ru.practicum.shareit.exception.ItemForbiddenException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
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
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(long userId, StandardItemDto itemDto) {
        checkIfUserExists(userId);
        Item item = ItemMapper.fromStandardItemDto(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toStandardItemDto(itemRepository.save(item), null);
    }

    @Override
    public ItemDto update(long itemId, long userId, StandardItemDto itemDto) {
        checkIfItemExists(itemId);
        Item updatedItem = itemRepository.getReferenceById(itemId);
        checkItemOwner(userId, updatedItem.getOwnerId());
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        List<CommentDto> comments = commentRepository
                .findAll()
                .stream()
                .filter(comment -> comment.getItem().getId() == itemId)
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toStandardItemDto(itemRepository.save(updatedItem), comments);
    }

    @Override
    public ItemDto get(long itemId, long ownerId) {
        checkIfItemExists(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        Optional<Booking> lastBooking = bookingRepository.getLastByItemId(itemId, ownerId).stream().findFirst();
        Optional<Booking> nextBooking = bookingRepository.getNextByItemId(itemId, ownerId).stream().findFirst();
        List<CommentDto> comments = commentRepository
                .findAll()
                .stream()
                .filter(comment -> comment.getItem().getId() == itemId)
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
        return items
                .stream()
                .map(item -> {
                    Optional<Booking> lastBooking = bookingRepository.getLastByItemId(item.getId(), ownerId).stream().findFirst();
                    Optional<Booking> nextBooking = bookingRepository.getNextByItemId(item.getId(), ownerId).stream().findFirst();
                    List<CommentDto> comments = commentRepository
                            .findAll()
                            .stream()
                            .filter(comment -> comment.getItem().getId().equals(item.getId()))
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    if (lastBooking.isEmpty() && nextBooking.isEmpty()) {
                        return ItemMapper.toStandardItemDto(item, comments);
                    } else {
                        return ItemMapper.toWithBookingItemDto(item, comments, lastBooking, nextBooking);
                    }
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
            throw new CommentBadRequestException(String.format("Нельзя добавить комментарий к вещи %d без бронирования", itemId));
        }
        if (bookingRepository.findAllCurrentByItemId(itemId).isEmpty()) {
            throw new CommentBadRequestException(String.format("Нельзя добавить комментарий к вещи %d с запланированными бронированиями.", itemId));
        }
    }
}