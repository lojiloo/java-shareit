package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.State;
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
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addNewBooking(long userId, CreateBookingRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateBookingStatus(long bookingId, boolean approved, long ownerId) {
        Map<String, Object> requestParam = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, requestParam, null);
    }

    public ResponseEntity<Object> getBookingInfoById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findByStateForBooker(State state, long bookerId) {
        Map<String, Object> requestParam = Map.of(
                "state", state.name()
        );
        return get("?state={state}", bookerId, requestParam);
    }

    public ResponseEntity<Object> findByStateForOwner(State state, long ownerId) {
        Map<String, Object> requestParam = Map.of(
                "state", state.name()
        );
        return get("/owner?state={state}", ownerId, requestParam);
    }

    public ResponseEntity<Object> getBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }
}
