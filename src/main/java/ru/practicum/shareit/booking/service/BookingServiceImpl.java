package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.booking.BookingBadRequestException;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.exception.item.ItemBadRequestException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
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
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final String ALL = "ALL";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
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

    @Transactional
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
        return BookingMapper.toStandardBookingDto(booking);
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
    public List<BookingDto> getAllByBooker(long bookerId, String status, int from, int size) {
        // Проверка
        checkIfUserExists(bookerId);
        checkRequestParams(from, size);
        if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            BookingStatus bookingStatus = EnumUtils.getEnum(BookingStatus.class, status);
            return findAllByBookerAndStatus(bookerId, bookingStatus, from, size);
        } else {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String status, int from, int size) {
        // Проверка
        checkIfUserExists(ownerId);
        checkRequestParams(from, size);
        if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            BookingStatus bookingStatus = EnumUtils.getEnum(BookingStatus.class, status);
            return findAllByOwnerAndStatus(ownerId, bookingStatus, from, size);
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
        if (start.equals(end)) {
            throw new BookingBadRequestException("Дата начала бронирования не может быть равна дате окончания.");
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

    private List<BookingDto> findAllByBookerAndStatus(long bookerId, BookingStatus status, int from, int size) {
        List<Booking> bookings;
        PageRequest pageRequest = page(from, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(bookerId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByBooker(bookerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBooker(bookerId, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByBooker(bookerId, pageRequest);
                break;
            default:
                bookings = bookingRepository.findAllByBookerAndStatus(bookerId, status, pageRequest);
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }

    private List<BookingDto> findAllByOwnerAndStatus(long ownerId, BookingStatus status, int from, int size) {
        List<Booking> bookings;
        PageRequest pageRequest = page(from, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(ownerId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByOwner(ownerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwner(ownerId, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByOwner(ownerId, pageRequest);
                break;
            default:
                bookings = bookingRepository.findAllByOwnerAndStatus(ownerId, status, pageRequest);
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }

    private void checkRequestParams(int from, int size) {
        if (from == 0 && size == 0) {
            throw new BookingBadRequestException("Параметры from и size не могут быть одновременно равны 0.");
        }
        if (from < 0 || size < 0) {
            throw new BookingBadRequestException("Параметры from и size не могут быть отрицательными.");
        }
    }

    private static PageRequest page(int from, int size, Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}