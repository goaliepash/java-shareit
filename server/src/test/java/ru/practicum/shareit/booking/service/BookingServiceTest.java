package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import static ru.practicum.shareit.utils.Creator.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    private void initBookingService() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void testCreate() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );
        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking1);

        BookingDto expectedBookingDto = BookingMapper.toStandardBookingDto(booking1);
        BookingDto actualBookingDto = bookingService.create(1L, bookingRequestDto1);

        Assertions.assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void testCreateNotExistItem() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(false);

        ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDto1));

        Assertions.assertEquals("Вещь с идентификатором 1 не найдена.", exception.getMessage());
    }

    @Test
    void testCreateItemNotAvailable() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", false, 2L);

        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);

        final ItemBadRequestException exception = Assertions.assertThrows(
                ItemBadRequestException.class,
                () -> bookingService.create(1L, bookingRequestDto1));

        Assertions.assertEquals("Вещь с идентификатором 1 не доступна.", exception.getMessage());
    }

    @Test
    void testCreateStartEqualsEnd() {
        LocalDateTime start = LocalDateTime.of(2022, 11, 22, 15, 48, 22);
        LocalDateTime end = LocalDateTime.of(2022, 11, 22, 15, 48, 22);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);

        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);

        final BookingBadRequestException exception = Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.create(1L, bookingRequestDto1));

        Assertions.assertEquals("Дата начала бронирования не может быть равна дате окончания.", exception.getMessage());
    }

    @Test
    void testCreateStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.of(2022, 11, 23, 15, 48, 22);
        LocalDateTime end = LocalDateTime.of(2022, 11, 22, 15, 48, 22);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);

        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);

        final BookingBadRequestException exception = Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.create(1L, bookingRequestDto1));

        Assertions.assertEquals("Дата начала бронирования указана позже даты окончания.", exception.getMessage());
    }

    @Test
    void testCreateWithOwnerEqualsBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );
        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);

        ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDto1)
        );

        Assertions.assertEquals("Невозможно завести бронь на свою же вещь.", exception.getMessage());
    }

    @Test
    void testUpdate() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking1);

        BookingDto expectedBookingDto = BookingMapper.toStandardBookingDto(booking1);
        BookingDto actualBookingDto = bookingService.update(2L, 1L, BookingStatus.WAITING);

        Assertions.assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void testUpdateBookingNotExist() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(false);

        BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.update(2L, 1L, BookingStatus.WAITING));

        Assertions.assertEquals("Бронирование с идентификатором 1 не найдено.", exception.getMessage());
    }

    @Test
    void testUpdateBookingWithWrongOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 3L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking1);

        BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.update(2L, 1L, BookingStatus.WAITING));

        Assertions.assertEquals("Неверно указан пользователь вещи.", exception.getMessage());
    }

    @Test
    void testUpdateWithApprovedStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 2L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.APPROVED,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking1);

        final BookingBadRequestException exception = Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.update(2L, 1L, BookingStatus.APPROVED)
        );

        Assertions.assertEquals("Статус бронирования 1 уже подтверждён.", exception.getMessage());
    }

    @Test
    void testGet() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking1));

        BookingDto expectedBookingDto = BookingMapper.toStandardBookingDto(booking1);
        BookingDto actualBookingDto = bookingService.get(1L, 1L);

        Assertions.assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void testGetBookingNotFoundException() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.get(1L, 1L)
        );

        Assertions.assertEquals("Бронирования с идентификатором 1 для пользователя 1 не найдено.", exception.getMessage());
    }

    @Test
    void testGetWithWrongUser() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);
        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.get(99L, 1L)
        );
        Assertions.assertEquals("Пользователь с идентификатором 99 не найден.", exception.getMessage());
    }

    @Test
    void testGetAllByBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBooker(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByBooker(1L, "ALL", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetPastByBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.PAST,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllPastByBooker(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByBooker(1L, "PAST", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetCurrentByBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.CURRENT,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllCurrentByBooker(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByBooker(1L, "CURRENT", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetFutureByBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.FUTURE,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllFutureByBooker(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByBooker(1L, "FUTURE", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetWaitingByBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito
                .when(bookingRepository.findAllByBookerAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByBooker(1L, "WAITING", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetAllByBookerUnsupportedStatus() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getAllByBooker(1L, "UNSUPPORTED", 0, 5));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void testGetAllByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito
                .when(bookingRepository.findAllByOwner(1L, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByOwner(1L, "ALL", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetPastByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.PAST,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllPastByOwner(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByOwner(1L, "PAST", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetCurrentByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.CURRENT,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllCurrentByOwner(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByOwner(1L, "CURRENT", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetFutureByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.FUTURE,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllFutureByOwner(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByOwner(1L, "FUTURE", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetWaitingByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        User user1 = createUser(1L, "Name 1", "email1@mail.ru");
        Booking booking1 = createBooking(
                1L,
                start,
                end,
                item1,
                BookingStatus.WAITING,
                user1
        );

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito
                .when(bookingRepository.findAllByOwnerAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        List<BookingDto> expectedBookings = List.of(BookingMapper.toStandardBookingDto(booking1));
        List<BookingDto> actualBookings = bookingService.getAllByOwner(1L, "WAITING", 0, 1);

        Assertions.assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void testGetAllByOwnerUnsupportedStatus() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getAllByOwner(1L, "UNSUPPORTED", 0, 5));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}