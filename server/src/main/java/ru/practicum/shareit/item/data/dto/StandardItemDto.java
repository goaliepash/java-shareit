package ru.practicum.shareit.item.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardItemDto implements ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}