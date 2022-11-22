package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.request.ItemRequestBadRequestException;
import ru.practicum.shareit.exception.request.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.request.data.ItemRequest;
import ru.practicum.shareit.request.data.dto.ItemRequestDto;
import ru.practicum.shareit.request.data.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        checkIfUserExists(userId);
        User requester = userRepository.getReferenceById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, requester);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(long requesterId) {
        checkIfUserExists(requesterId);
        return itemRequestRepository
                .getByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        checkIfUserExists(userId);
        checkRequestParams(from, size);
        return itemRequestRepository
                .findAllByUserId(userId, page(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto get(long userId, long requestId) {
        checkIfUserExists(userId);
        checkIfItemRequestExists(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.getReferenceById(requestId));
    }

    private void checkIfUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    private void checkRequestParams(int from, int size) {
        if (from == 0 && size == 0) {
            throw new ItemRequestBadRequestException("Параметры from и size не могут быть одновременно равны 0.");
        }
        if (from < 0 || size < 0) {
            throw new ItemRequestBadRequestException("Параметры from и size не могут быть отрицательными.");
        }
    }

    private void checkIfItemRequestExists(long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ItemRequestNotFoundException(String.format("Запроса с идентификатором %d не существует.", requestId));
        }
    }

    private static PageRequest page(int from, int size, Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}