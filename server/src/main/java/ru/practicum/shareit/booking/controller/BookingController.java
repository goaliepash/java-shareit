package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

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
            @RequestBody BookingRequestDto createBookingDto) {
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
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
        log.info("Выполнен запрос GET /bookings?state={}&from={}&size={}.", status, from, size);
        return service.getAllByBooker(bookerId, status, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, name = "state", defaultValue = "ALL") String status,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
        log.info("Выполнен запрос GET /bookings/owner?state={}&from={}&size={}.", status, from, size);
        return service.getAllByOwner(ownerId, status, from, size);
    }
}