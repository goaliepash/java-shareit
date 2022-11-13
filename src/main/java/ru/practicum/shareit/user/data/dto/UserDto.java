package ru.practicum.shareit.user.data.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.constraint_group.Create;
import ru.practicum.shareit.constraint_group.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;
}