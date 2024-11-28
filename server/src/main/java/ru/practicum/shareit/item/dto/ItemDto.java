package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();
}
