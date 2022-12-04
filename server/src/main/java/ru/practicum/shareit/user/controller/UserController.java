package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.data.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        log.info("Выполнен запрос POST /users.");
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto user) {
        log.info("Выполнен запрос PATCH /users/{}.", userId);
        return service.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Выполнен запрос GET /users/{}.", userId);
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Выполнен запрос DELETE /users/{}.", userId);
        service.delete(userId);
    }

    @GetMapping
    public List<UserDto> get() {
        log.info("Выполнен запрос GET /users.");
        return service.get();
    }
}