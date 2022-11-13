package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortBookingDto implements BookingDto {
    private Long id;
    private Long bookerId;
}