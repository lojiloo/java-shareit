package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRequestDto {
    @NotBlank(message = "Запрос должен содержать описание вещи")
    @Size(max = 500)
    String description;
}
