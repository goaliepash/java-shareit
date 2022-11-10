package ru.practicum.shareit.item.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.constraint_group.Create;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class CommentRequestDto {
    @NotBlank(groups = {Create.class})
    @JsonProperty("text")
    private String text;
}