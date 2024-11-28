package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateItem {
    @NotBlank(message = "Наименование вещи не может быть пустым")
    @Size(max = 100)
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(max = 500)
    private String description;
    @NotNull(message = "Необходимо указать, доступна ли на данный момент вещь к бронированию")
    private Boolean available;
    private Long requestId;
}
