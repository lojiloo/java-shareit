package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {
    @Size(max = 250)
    private String name;
    @Size(max = 250)
    @Email(message = "Некорректный формат email")
    private String email;
}
