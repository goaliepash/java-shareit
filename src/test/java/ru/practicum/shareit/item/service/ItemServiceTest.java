package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.data.Comment;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.item.data.dto.*;
import ru.practicum.shareit.item.data.mapper.CommentMapper;
import ru.practicum.shareit.item.data.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.utils.Creator.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    private void initItemService() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    public void testCreate() {
        Item createdItem = createItem(1L, "Name", "Desc", true, 1L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(createdItem);

        ItemDto expectedItemDto = ItemMapper.toStandardItemDto(createdItem, null);
        ItemDto actualItemDto = itemService.create(1L, StandardItemDto.builder().name("Name").description("Desc").available(true).build());

        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    public void testUpdate() {
        Item updatedItem = createItem(1L, "Name", "Desc", true, 1L);

        Mockito.when(itemRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(updatedItem);

        ItemDto expectedItemDto = StandardItemDto
                .builder()
                .id(1L)
                .name("Name Update")
                .description("Desc Update")
                .available(true)
                .comments(List.of())
                .build();
        ItemDto actualItemDto = itemService.update(
                1L,
                1L,
                StandardItemDto.builder().name("Name Update").description("Desc Update").available(true).build()
        );

        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    public void testGet() {
        Item item = createItem(1L, "Name", "Desc", true, 1L);

        Mockito.when(itemRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);

        ItemDto expectedItemDto = StandardItemDto
                .builder()
                .id(1L)
                .name("Name")
                .description("Desc")
                .available(true)
                .comments(List.of())
                .build();
        ItemDto actualItemDto = itemService.get(1L, 1L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    public void testGetWithBooking() {
        Item item = createItem(1L, "Name", "Desc", true, 1L);

        User booker = new User();
        booker.setId(1L);

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBooker(booker);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);

        Mockito.when(itemRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito
                .when(bookingRepository.getLastByItemId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(lastBooking));
        Mockito
                .when(bookingRepository.getNextByItemId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(nextBooking));

        ItemDto expectedItemDto = WithBookingItemDto
                .builder()
                .id(1L)
                .name("Name")
                .description("Desc")
                .available(true)
                .comments(List.of())
                .lastBooking(ShortBookingDto.builder().id(1L).bookerId(1L).build())
                .nextBooking(ShortBookingDto.builder().id(2L).bookerId(1L).build())
                .build();
        ItemDto actualItemDto = itemService.get(1L, 1L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    public void testGetAll() {
        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        Item item2 = createItem(2L, "Name 2", "Desc 2", true, 1L);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));

        List<ItemDto> expectedItems = List.of(
                ItemMapper.toStandardItemDto(item1, null),
                ItemMapper.toStandardItemDto(item2, null)
        );
        List<ItemDto> actualItems = itemService.get(1L);

        Assertions.assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testGetAllWithCommentsAndBookings() {
        Item item2 = createItem(2L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");

        Comment comment1 = createComment(
                1L,
                "Add comment from user 1",
                item2,
                user1,
                LocalDateTime.of(2022, 11, 23, 15, 10, 18)
        );

        Booking booking1 = createBooking(
                1L,
                LocalDateTime.of(2022, 11, 23, 15, 10, 13),
                LocalDateTime.of(2022, 11, 23, 15, 10, 14),
                item2,
                BookingStatus.APPROVED,
                user1
        );
        Booking booking2 = createBooking(
                2L,
                LocalDateTime.of(2022, 11, 24, 15, 10, 10),
                LocalDateTime.of(2022, 11, 25, 15, 10, 10),
                item2,
                BookingStatus.APPROVED,
                user1
        );

        Mockito.when(userRepository.existsById(4L)).thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(4L)).thenReturn(List.of(item2));
        Mockito.when(commentRepository.findAllByOwnerId(4L)).thenReturn(List.of(comment1));
        Mockito
                .when(bookingRepository.findAllByOwner(4L, Sort.by(Sort.Direction.ASC, "start")))
                .thenReturn(List.of(booking1));

        List<ItemDto> expectedItems = List.of(
                ItemMapper.toStandardItemDto(item2, List.of(CommentMapper.toCommentDto(comment1)))
        );
        List<ItemDto> actualItems = itemService.get(4L);

        Assertions.assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testSearch() {
        Item item1 = createItem(1L, "Name 1", "Desc 1", true, 1L);
        Item item2 = createItem(2L, "Name 2", "Desc 2", true, 1L);

        Mockito.when(itemRepository.findAllByText(Mockito.any())).thenReturn(List.of(item1, item2));

        List<ItemDto> expectedItems = List.of(
                ItemMapper.toStandardItemDto(item1, null),
                ItemMapper.toStandardItemDto(item2, null)
        );
        List<ItemDto> actualItems = itemService.search("Name");

        Assertions.assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testAddComment() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        Booking booking1 = createBooking(
                1L,
                LocalDateTime.of(2022, 11, 23, 15, 10, 13),
                LocalDateTime.of(2022, 11, 23, 15, 10, 14),
                item1,
                BookingStatus.APPROVED,
                user1
        );
        Comment comment1 = createComment(
                1L,
                "Add comment from user 1",
                item1,
                user1,
                LocalDateTime.of(2022, 11, 23, 15, 10, 18)
        );

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Add comment from user 1");

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.existsById(1L)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItemIdAndStatus(1L, BookingStatus.REJECTED)).thenReturn(List.of(booking1));
        Mockito.when(bookingRepository.findAllCurrentByItemId(1L)).thenReturn(List.of(booking1));
        Mockito.when(userRepository.getReferenceById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.getReferenceById(1L)).thenReturn(item1);
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment1);

        CommentDto expectedCommentDto = CommentMapper.toCommentDto(comment1);
        CommentDto actualCommentDto = itemService.addComment(1L, 1L, commentRequestDto);

        Assertions.assertEquals(expectedCommentDto, actualCommentDto);
    }
}