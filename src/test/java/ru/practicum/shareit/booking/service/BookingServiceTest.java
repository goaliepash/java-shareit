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
    public void testCreate() {
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
        BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder().start(start).end(end).itemId(1L).build();

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
    public void testUpdate() {
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
    public void testGet() {
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
    public void testGetAllByBooker() {
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
    public void testGetAllByOwner() {
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
}