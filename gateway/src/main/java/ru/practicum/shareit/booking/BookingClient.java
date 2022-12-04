package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookingRequestDto createBookingDto) {
        return post("", userId, createBookingDto);
    }

    public ResponseEntity<Object> update(long ownerId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }

    public ResponseEntity<Object> get(long userId, long bookingId) {
        return get(String.format("/%d", bookingId), userId);
    }

    public ResponseEntity<Object> getAllByBooker(long bookerId, String status, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(long ownerId, String status, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}