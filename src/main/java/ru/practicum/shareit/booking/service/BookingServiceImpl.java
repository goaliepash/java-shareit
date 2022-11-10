package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String ALL = "ALL";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(long userId, BookingRequestDto createBookingDto) {
        // Проверки
        checkIfUserExists(userId);
        checkIfItemExists(createBookingDto.getItemId());
        checkIfItemAvailable(createBookingDto.getItemId());
        checkCorrectDateTimePeriod(createBookingDto.getStart(), createBookingDto.getEnd());
        // Получить пользователя
        User booker = userRepository.getReferenceById(userId);
        // Получить вещь для бронирования
        Item item = itemRepository.getReferenceById(createBookingDto.getItemId());
        checkUserOwnItem(item.getOwnerId(), booker.getId());
        // Сформировать бронирование
        Booking booking = BookingMapper.fromBookingRequestDto(createBookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        // Сохранить бронирование в БД
        Booking createdBooking = bookingRepository.save(booking);
        return BookingMapper.toStandardBookingDto(createdBooking);
    }

    @Override
    public BookingDto update(long ownerId, long bookingId, BookingStatus status) {
        // Проверки
        checkIfUserExists(ownerId);
        checkIfBookingExists(bookingId);
        // Получить бронирование
        Booking booking = bookingRepository.getReferenceById(bookingId);
        // Проверить владельца вещи
        checkCorrectItemOwner(ownerId, booking.getItem().getOwnerId());
        // Изменить статус бронирования и сохранить
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingBadRequestException(String.format("Статус бронирования %d уже подтверждён.", bookingId));
        }
        booking.setStatus(status);
        return BookingMapper.toStandardBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto get(long userId, long bookingId) {
        // Проверки
        checkIfUserExists(userId);
        // Получить бронирование
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwnerId()) {
                return BookingMapper.toStandardBookingDto(booking);
            }
        }
        throw new BookingNotFoundException(String.format("Бронирования с идентификатором %d для пользователя %d не найдено.", bookingId, userId));
    }

    @Override
    public List<BookingDto> getAllByBooker(long bookerId, String status) {
        // Проверка
        checkIfUserExists(bookerId);
        if (status.equals(ALL)) {
            return findAllByBooker(bookerId);
        } else if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            return findAllByBookerAndStatus(bookerId, EnumUtils.getEnum(BookingStatus.class, status));
        } else if (EnumUtils.isValidEnum(BookingState.class, status)) {
            return findAllByBookerAndState(bookerId, EnumUtils.getEnum(BookingState.class, status));
        } else {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String status) {
        // Проверка
        checkIfUserExists(ownerId);
        if (status.equals(ALL)) {
            return findAllByOwner(ownerId);
        } else if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            return findAllByOwnerAndStatus(ownerId, EnumUtils.getEnum(BookingStatus.class, status));
        } else if (EnumUtils.isValidEnum(BookingState.class, status)) {
            return findAllByOwnerAndState(ownerId, EnumUtils.getEnum(BookingState.class, status));
        } else {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
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

    private void checkIfItemAvailable(long itemId) {
        if (!itemRepository.getReferenceById(itemId).getAvailable()) {
            throw new ItemBadRequestException(String.format("Вещь с идентификатором %d не доступна.", itemId));
        }
    }

    private void checkCorrectDateTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("Дата начала бронирования указана в прошедшем времени.");
        }
        if (end.isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("Дата окончания бронирования указана в прошедшем времени.");
        }
        if (start.isAfter(end)) {
            throw new BookingBadRequestException("Дата начала бронирования указана позже даты окончания.");
        }
    }

    private void checkIfBookingExists(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(String.format("Бронирование с идентификатором %d не найдено.", bookingId));
        }
    }

    private void checkCorrectItemOwner(long expectedOwnerId, long actualOwnerId) {
        if (expectedOwnerId != actualOwnerId) {
            throw new BookingNotFoundException("Неверно указан пользователь вещи.");
        }
    }

    private void checkUserOwnItem(long ownerId, long bookerId) {
        if (ownerId == bookerId) {
            throw new ItemNotFoundException("Невозможно завести бронь на свою же вещь.");
        }
    }

    private List<BookingDto> findAllByBooker(long bookerId) {
        return bookingRepository
                .findAllByBooker(bookerId)
                .stream()
                .map(BookingMapper::toStandardBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> findAllByBookerAndStatus(long bookerId, BookingStatus status) {
        return bookingRepository
                .findAllByBookerAndStatus(bookerId, status)
                .stream()
                .map(BookingMapper::toStandardBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> findAllByBookerAndState(long bookerId, BookingState state) {
        List<Booking> bookings;
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllPastByBooker(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBooker(bookerId);
                break;
            default:
                bookings = bookingRepository.findAllFutureByBooker(bookerId);
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }

    private List<BookingDto> findAllByOwner(long ownerId) {
        return bookingRepository
                .findAllByOwner(ownerId)
                .stream()
                .map(BookingMapper::toStandardBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> findAllByOwnerAndStatus(long ownerId, BookingStatus status) {
        return bookingRepository
                .findAllByOwnerAndStatus(ownerId, status)
                .stream()
                .map(BookingMapper::toStandardBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> findAllByOwnerAndState(long ownerId, BookingState state) {
        List<Booking> bookings;
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllPastByOwner(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwner(ownerId);
                break;
            default:
                bookings = bookingRepository.findAllFutureByOwner(ownerId);
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }
}