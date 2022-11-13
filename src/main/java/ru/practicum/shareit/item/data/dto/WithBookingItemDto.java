package ru.practicum.shareit.item.data.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;

import java.util.List;

@Data
@Builder
public class WithBookingItemDto implements ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentDto> comments;
}