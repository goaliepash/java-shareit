package ru.practicum.shareit.item.data.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;
import ru.practicum.shareit.constraint_group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class WithBookingItemDto implements ItemDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}