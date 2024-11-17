package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(CreateUserRequest request) {
        User user = modelMapper.map(request, User.class);
        if (userStorage.findByEmailLike(user.getEmail()).isPresent()) {
            throw new ConflictException("Данный email уже занят другим пользователем");
        }

        return modelMapper.map(userStorage.save(user), UserDto.class);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request, long userId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не был найден")
        );

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (userStorage.findByEmailLikeAndIdNot(user.getEmail(), user.getId()).isPresent()) {
            throw new ConflictException("Данный email уже занят другим пользователем");
        }

        return modelMapper.map(userStorage.save(user), UserDto.class);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userStorage.getReferenceById(userId);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.findAll();
        List<UserDto> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(modelMapper.map(user, UserDto.class));
        }

        return dtos;
    }

    @Override
    public void deleteUser(long userId) {
        if (userStorage.existsById(userId)) {
            userStorage.deleteById(userId);
        }
    }
}
