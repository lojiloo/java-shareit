package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private long itemId;
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
}
