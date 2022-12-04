package ru.practicum.shareit.item.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommentRequestDto {
    @JsonProperty("text")
    private String text;
}