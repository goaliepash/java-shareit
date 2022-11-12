package ru.practicum.shareit.item.data.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ShortBookingDto;
import ru.practicum.shareit.item.data.Item;
import ru.practicum.shareit.item.data.dto.CommentDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;
import ru.practicum.shareit.item.data.dto.WithBookingItemDto;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static StandardItemDto toStandardItemDto(Item item, List<CommentDto> comments) {
        return StandardItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public static WithBookingItemDto toWithBookingItemDto(Item item, List<CommentDto> comments, Optional<Booking> lastBooking, Optional<Booking> nextBooking) {
        return WithBookingItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking.map(booking -> ShortBookingDto.builder().id(booking.getId()).bookerId(booking.getBooker().getId()).build()).orElse(null))
                .nextBooking(nextBooking.map(booking -> ShortBookingDto.builder().id(booking.getId()).bookerId(booking.getBooker().getId()).build()).orElse(null))
                .comments(comments)
                .build();
    }

    public static Item fromStandardItemDto(StandardItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}