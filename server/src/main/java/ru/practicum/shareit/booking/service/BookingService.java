package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addNewBooking(CreateBookingDto request, long bookerId);

    BookingDto updateBookingStatus(long bookingId, boolean approved, long ownerId);

    BookingDto getBookingInfoById(long bookingId, long userId);

    List<BookingDto> findByStateForBooker(State state, long bookerId);

    List<BookingDto> findByStateForOwner(State state, long ownerId);

}
