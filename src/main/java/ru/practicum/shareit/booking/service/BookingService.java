package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {

    BookingDto addNewBooking(CreateBookingRequest request, long bookerId);

    BookingDto updateBookingStatus(long bookingId, boolean approved, long ownerId);

    BookingDto getBookingInfoById(long bookingId, long userId);

    List<BookingDto> findByStateForBooker(String state, long bookerId);

    List<BookingDto> findByStateForOwner(String state, long ownerId);

}
