package ru.practicum.shareit.item.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    String text;
    String itemName;
    String authorName;
    LocalDateTime created;
}
