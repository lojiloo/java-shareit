package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItem {
    @Size(max = 100)
    private String name;
    @Size(max = 500)
    private String description;
    private Boolean available;
}
