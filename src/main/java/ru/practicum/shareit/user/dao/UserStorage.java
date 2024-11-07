package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User getUserById(long userId);

    List<User> getAll();

    void deleteUser(long userId);

    boolean contains(long userId);

    void validateNewUser(User user);

    void validateUpdatedUser(User user);

}
