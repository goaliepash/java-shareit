package ru.practicum.shareit.item.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortItemDto {
    private long id;
    private String name;
}