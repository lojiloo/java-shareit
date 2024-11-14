package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(UpdateUserRequest request, long userId);

    UserDto getUserDtoById(long userId);

    List<UserDto> getAllDtos();

    void deleteUser(long userId);
}
