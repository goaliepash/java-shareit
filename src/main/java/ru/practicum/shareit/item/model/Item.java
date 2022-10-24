package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private String request;
}