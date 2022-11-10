package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constraint_group.Create;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody BookingRequestDto createBookingDto) {
        log.info("Выполнен запрос POST /bookings.");
        return service.create(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable long bookingId,
            @RequestParam(required = true, name = "approved") boolean approved) {
        log.info("Выполнен запрос PATCH /bookings/{}?approved={}.", bookingId, approved);
        return service.update(ownerId, bookingId, approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Выполнен запрос GET /bookings/{}.", bookingId);
        return service.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(required = false, name = "state", defaultValue = "ALL") String status) {
        log.info("Выполнен запрос GET /bookings?state={}.", status);
        return service.getAllByBooker(bookerId, status);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, name = "state", defaultValue = "ALL") String status) {
        log.info("Выполнен запрос GET /bookings/owner?state={}.", status);
        return service.getAllByOwner(ownerId, status);
    }
}