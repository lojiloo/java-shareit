package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
