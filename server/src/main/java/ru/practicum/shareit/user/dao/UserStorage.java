package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage extends JpaRepository<User, Long> {

    Optional<User> findByEmailLike(String email);

    Optional<User> findByEmailLikeAndIdNot(String email, long id);

}
