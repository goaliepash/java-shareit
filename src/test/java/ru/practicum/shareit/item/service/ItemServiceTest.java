package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.item.data.dto.ItemDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;
import ru.practicum.shareit.item.data.dto.WithBookingItemDto;
import ru.practicum.shareit.item.data.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

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
        Item createdItem = new Item();
        createdItem.setId(1L);
        createdItem.setName("Name");
        createdItem.setDescription("Desc");
        createdItem.setAvailable(true);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(createdItem);

        ItemDto expectedItemDto = ItemMapper.toStandardItemDto(createdItem, null);
        ItemDto actualItemDto = itemService.create(1L, StandardItemDto.builder().name("Name").description("Desc").available(true).build());

        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    public void testUpdate() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Name");
        updatedItem.setDescription("Desc");
        updatedItem.setAvailable(true);
        updatedItem.setOwnerId(1L);

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
        Item item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwnerId(1L);

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
        Item item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwnerId(1L);

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
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Name 1");
        item1.setDescription("Desc 1");
        item1.setAvailable(true);
        item1.setOwnerId(1L);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Name 2");
        item2.setDescription("Desc 2");
        item2.setAvailable(true);
        item2.setOwnerId(1L);

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
    public void testSearch() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Name 1");
        item1.setDescription("Desc 1");
        item1.setAvailable(true);
        item1.setOwnerId(1L);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Name 2");
        item2.setDescription("Desc 2");
        item2.setAvailable(true);
        item2.setOwnerId(1L);

        Mockito.when(itemRepository.findAllByText(Mockito.any())).thenReturn(List.of(item1, item2));

        List<ItemDto> expectedItems = List.of(
                ItemMapper.toStandardItemDto(item1, null),
                ItemMapper.toStandardItemDto(item2, null)
        );
        List<ItemDto> actualItems = itemService.search("Name");

        Assertions.assertEquals(expectedItems, actualItems);
    }

    private Item createItem(long id, String name, String description, boolean available, long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }
}