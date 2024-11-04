package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    @Qualifier("InMemoryUsers")
    private final UserStorage userStorage;

    @Override
    public User createUser(CreateUserRequest request) {
        User user = modelMapper.map(request, User.class);
        userStorage.validateNewUser(user);

        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(UpdateUserRequest request, long userId) {
        User user = modelMapper.map(request, User.class);
        user.setId(userId);
        userStorage.validateUpdatedUser(user);

        return userStorage.updateUser(user);
    }

    @Override
    public UserDto getUserDtoById(long userId) {
        User user = userStorage.getUserById(userId);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getAllDtos() {
        List<User> users = userStorage.getAll();
        List<UserDto> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(modelMapper.map(user, UserDto.class));
        }

        return dtos;
    }

    @Override
    public void deleteUser(long userId) {
        if (userStorage.contains(userId)) {
            userStorage.deleteUser(userId);
        }
    }
}
