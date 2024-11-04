package ru.practicum.shareit.user.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("InMemoryUsers")
@Data
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long userId = 0L;

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        long id = user.getId();
        if (user.getName() == null) user.setName(users.get(id).getName());
        if (user.getEmail() == null) user.setEmail(users.get(id).getEmail());
        users.put(id, user);

        return user;
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean contains(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public void validateNewUser(User user) {
        String email = user.getEmail();
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                throw new ConflictException("Данный email уже занят другим пользователем");
            }
        }
    }

    @Override
    public void validateUpdatedUser(User user) {
        String email = user.getEmail();
        for (User u : users.values()) {
            if (u.getEmail().equals(email) && u.getId() != user.getId()) {
                throw new ConflictException("Данный email уже занят другим пользователем");
            }
        }
    }

    protected long getNextId() {
        return ++userId;
    }
}
