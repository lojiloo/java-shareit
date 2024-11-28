package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDtoWithItems {
    Long id;
    String description;
    UserDto requester;
    LocalDateTime created;
    List<ItemDtoForItemRequest> items;
}
