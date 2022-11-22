package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.request.data.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполнен запрос POST /requests.");
        return service.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequesterId(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.info("Выполнен запрос GET /requests.");
        return service.getByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
            log.info("Выполнен запрос GET /requests/all?from={}&size={}", from, size);
            return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Выполнен запрос GET /requests/{}.", requestId);
        return service.get(userId, requestId);
    }
}