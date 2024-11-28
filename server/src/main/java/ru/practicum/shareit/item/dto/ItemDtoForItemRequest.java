package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDtoForItemRequest {
    Long id;
    String name;
    Long ownerId;
}
