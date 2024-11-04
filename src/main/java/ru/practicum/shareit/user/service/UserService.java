package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(CreateUserRequest request);

    User updateUser(UpdateUserRequest request, long userId);

    UserDto getUserDtoById(long userId);

    List<UserDto> getAllDtos();

    void deleteUser(long userId);
}
