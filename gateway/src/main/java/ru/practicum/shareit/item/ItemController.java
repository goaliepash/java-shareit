package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.constraint_group.Update;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.StandardItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос POST /items.");
        return client.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Update.class) @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос PATCH /items/{}.", itemId);
        return client.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Выполнен запрос GET /items/{}.", itemId);
        return client.get(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выполнен запрос GET /items.");
        return client.get(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false, name = "text", defaultValue = "") String text) {
        log.info("Выполнен запрос GET /items/search?text={}.", text);
        return client.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Выполнен запрос POST /{}/comment.", itemId);
        return client.addComment(itemId, userId, commentRequestDto);
    }
}