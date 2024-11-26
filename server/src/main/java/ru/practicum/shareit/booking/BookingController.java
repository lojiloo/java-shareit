package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDto addNewBooking(@RequestBody CreateBookingDto request,
                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.addNewBooking(request, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable long bookingId,
                                          @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfoById(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingInfoById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findByStateForBooker(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        State stateFromRequest;
        try {
            stateFromRequest = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неверно указан параметр запроса", e);
        }

        return bookingService.findByStateForBooker(stateFromRequest, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByStateForOwner(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                @RequestHeader("X-Sharer-User-Id") long ownerId) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Неверно указан параметр запроса"));
        return bookingService.findByStateForOwner(state, ownerId);
    }

}
