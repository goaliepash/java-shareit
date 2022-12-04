package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполнен запрос POST /requests.");
        return client.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequesterId(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.info("Выполнен запрос GET /requests.");
        return client.getByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
        log.info("Выполнен запрос GET /requests/all?from={}&size={}", from, size);
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Выполнен запрос GET /requests/{}.", requestId);
        return client.get(userId, requestId);
    }
}