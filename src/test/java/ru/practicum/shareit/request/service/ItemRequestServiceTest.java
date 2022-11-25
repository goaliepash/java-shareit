package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.request.data.ItemRequest;
import ru.practicum.shareit.request.data.dto.ItemRequestDto;
import ru.practicum.shareit.request.data.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.shareit.utils.Creator.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    private void initItemRequestService() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
    }

    @Test
    public void testCreate() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Desc 1");

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);

        ItemRequestDto expectedItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        ItemRequestDto actualItemRequestDto = itemRequestService.create(1L, itemRequestDto);

        Assertions.assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    public void testGetByRequesterId() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getByRequesterId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedRequests = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));
        List<ItemRequestDto> actualRequests = itemRequestService.getByRequesterId(1L);

        Assertions.assertEquals(expectedRequests, actualRequests);
    }

    @Test
    public void testGetAll() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito
                .when(itemRequestRepository.findAllByUserId(1L, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedRequests = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));
        List<ItemRequestDto> actualRequests = itemRequestService.getAll(1L, 0, 1);

        Assertions.assertEquals(expectedRequests, actualRequests);
    }

    @Test
    public void testGet() {
        Item item1 = createItem(1L, "Отвертка", "Аккумуляторная отвертка", true, 4L);
        User user1 = createUser(1L, "updateName", "updateName@user.com");
        ItemRequest itemRequest = createItemRequest(1L, "Desc 1", user1, LocalDateTime.now(), Set.of(item1));

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getReferenceById(1L)).thenReturn(itemRequest);

        ItemRequestDto expectedItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        ItemRequestDto actualItemRequestDto = itemRequestService.get(1L, 1L);

        Assertions.assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }
}