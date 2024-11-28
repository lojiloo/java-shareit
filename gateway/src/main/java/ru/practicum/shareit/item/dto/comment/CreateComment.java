package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateComment {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 1000)
    String text;
}