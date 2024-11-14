package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

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
    public BookingDto addNewBooking(@RequestBody CreateBookingRequest request,
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
        return bookingService.findByStateForBooker(state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByStateForOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.findByStateForOwner(state, ownerId);
    }

}
