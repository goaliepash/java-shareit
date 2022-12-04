package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.data.dto.CommentDto;
import ru.practicum.shareit.item.data.dto.CommentRequestDto;
import ru.practicum.shareit.item.data.dto.ItemDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос POST /items.");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос PATCH /items/{}.", itemId);
        return service.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Выполнен запрос GET /items/{}.", itemId);
        return service.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выполнен запрос GET /items.");
        return service.get(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false, name = "text", defaultValue = "") String text) {
        log.info("Выполнен запрос GET /items/search?text={}.", text);
        if (text.isBlank()) {
            return List.of();
        } else {
            return service.search(text);
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Выполнен запрос POST /{}/comment.", itemId);
        return service.addComment(itemId, userId, commentRequestDto);
    }
}