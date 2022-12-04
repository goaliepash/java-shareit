package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.exception.BookingBadRequestException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Validated
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody BookingRequestDto createBookingDto) {
        log.info("Выполнен запрос POST /bookings.");
        checkCorrectDateTimePeriod(createBookingDto.getStart(), createBookingDto.getEnd());
        return client.create(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        log.info("Выполнен запрос PATCH /bookings/{}?approved={}.", bookingId, approved);
        return client.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Выполнен запрос GET /bookings/{}.", bookingId);
        return client.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String status,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
        log.info("Выполнен запрос GET /bookings?state={}&from={}&size={}.", status, from, size);
        return client.getAllByBooker(bookerId, status, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, name = "state", defaultValue = "ALL") String status,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "5") int size) {
        log.info("Выполнен запрос GET /bookings/owner?state={}&from={}&size={}.", status, from, size);
        return client.getAllByOwner(ownerId, status, from, size);
    }

    private void checkCorrectDateTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            throw new BookingBadRequestException("Дата начала бронирования не может быть равна дате окончания.");
        }
        if (start.isAfter(end)) {
            throw new BookingBadRequestException("Дата начала бронирования указана позже даты окончания.");
        }
    }
}