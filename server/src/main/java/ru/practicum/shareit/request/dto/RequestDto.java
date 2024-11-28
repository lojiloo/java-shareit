package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    Long id;
    String description;
    UserDto requester;
    LocalDateTime created;
}
