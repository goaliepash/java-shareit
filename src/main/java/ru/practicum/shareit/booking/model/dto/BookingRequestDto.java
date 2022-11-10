package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.constraint_group.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto implements BookingDto {

    @NotNull(groups = {Create.class})
    private Long itemId;

    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    private LocalDateTime end;
}