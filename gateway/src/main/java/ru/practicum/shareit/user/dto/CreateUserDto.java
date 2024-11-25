package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 250)
    private String name;
    @NotBlank(message = "Email пользователя не может быть пустым")
    @Size(max = 250)
    @Email(message = "Некорректный формат email")
    private String email;
}
