package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long bookingId;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private User owner;
}
