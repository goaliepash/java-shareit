package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.dto.StandardBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.data.dto.ShortItemDto;
import ru.practicum.shareit.user.data.dto.BookerDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testCreate() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        StandardBookingDto standardBookingDto = new StandardBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(1L, "Name")
        );
        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any())).thenReturn(standardBookingDto);
        mvc
                .perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(standardBookingDto.getId()), Long.class));
    }

    @Test
    void testUpdate() throws Exception {
        StandardBookingDto standardBookingDto = new StandardBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(1L, "Name")
        );
        Mockito.when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(standardBookingDto);
        mvc
                .perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(standardBookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(standardBookingDto.getId()), Long.class));
    }

    @Test
    void testGet() throws Exception {
        StandardBookingDto standardBookingDto = new StandardBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(1L, "Name")
        );
        Mockito.when(bookingService.get(Mockito.anyLong(), Mockito.anyLong())).thenReturn(standardBookingDto);
        mvc
                .perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(standardBookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(standardBookingDto.getId()), Long.class));
    }

    @Test
    void testGetAllByBooker() throws Exception {
        StandardBookingDto standardBookingDto1 = new StandardBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(1L, "Name")
        );
        StandardBookingDto standardBookingDto2 = new StandardBookingDto(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(2L, "Name")
        );
        Mockito
                .when(bookingService.getAllByBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(standardBookingDto1, standardBookingDto2));
        mvc
                .perform(get("/bookings?state=WAITING&from=0&size=2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllByOwner() throws Exception {
        StandardBookingDto standardBookingDto1 = new StandardBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(1L, "Name")
        );
        StandardBookingDto standardBookingDto2 = new StandardBookingDto(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new BookerDto(1L),
                new ShortItemDto(2L, "Name")
        );
        Mockito
                .when(bookingService.getAllByOwner(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(standardBookingDto1, standardBookingDto2));
        mvc
                .perform(get("/bookings/owner?state=WAITING&from=0&size=2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}