package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.List;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid CreateBookingRequest request) {
        log.info("Поступил запрос на бронирование вещи с id={}", request.getItemId());
        return bookingClient.addNewBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@PathVariable long bookingId,
                                                      @RequestParam boolean approved,
                                                      @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Поступил запрос на обновление статуса заявки на бронирование; заявка принята={}", approved);
        return bookingClient.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId) {
        log.info("Поступил запрос от пользователя с userid={} на предоставление информации о бронировании с id={}", userId, bookingId);
        return bookingClient.getBookingInfoById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByStateForBooker(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                       @RequestHeader("X-Sharer-User-Id") long bookerId) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Неверно указан параметр запроса"));
        log.info("Поступил запрос от пользователя с id={} на просмотр всех бронирований со статусом {}", bookerId, state);
        return bookingClient.findByStateForBooker(state, bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByStateForOwner(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestHeader("X-Sharer-User-Id") long ownerId) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Неверно указан параметр запроса"));
        log.info("Поступил запрос на просмотр всех забронированных вещей со статусом бронирования {}, принадлежащих пользователю с id={}",
                state, ownerId);
        return bookingClient.findByStateForOwner(state, ownerId);
    }
}
