package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.constraint_group.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("Выполнен запрос POST /users.");
        return client.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId, @Validated(Update.class) @RequestBody UserDto user) {
        log.info("Выполнен запрос PATCH /users/{}.", userId);
        return client.update(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        log.info("Выполнен запрос GET /users/{}.", userId);
        return client.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Выполнен запрос DELETE /users/{}.", userId);
        return client.delete(userId);
    }

    @GetMapping
    public ResponseEntity<Object> get() {
        log.info("Выполнен запрос GET /users.");
        return client.get();
    }
}