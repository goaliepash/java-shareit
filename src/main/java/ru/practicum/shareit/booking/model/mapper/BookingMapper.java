package ru.practicum.shareit.booking.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.dto.StandardBookingDto;
import ru.practicum.shareit.item.data.dto.ShortItemDto;
import ru.practicum.shareit.user.data.dto.BookerDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking fromBookingRequestDto(BookingRequestDto createBookingDto) {
        Booking booking = new Booking();
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        return booking;
    }

    public static StandardBookingDto toStandardBookingDto(Booking booking) {
        return StandardBookingDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(BookerDto.builder().id(booking.getBooker().getId()).build())
                .item(ShortItemDto.builder().id(booking.getItem().getId()).name(booking.getItem().getName()).build())
                .build();
    }
}