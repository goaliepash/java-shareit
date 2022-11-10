package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.data.dto.ShortItemDto;
import ru.practicum.shareit.user.data.dto.BookerDto;

import java.time.LocalDateTime;

@Data
@Builder
public class StandardBookingDto implements BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerDto booker;
    private ShortItemDto item;
}