package ru.practicum.shareit.request.data.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.item.data.dto.StandardItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String description;
    private LocalDateTime created;
    private Set<StandardItemDto> items;
}