package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserStorageTests {
    @Autowired
    private UserStorage storage;

    @Test
    public void findByEmailLikeTest() {
        User user1 = new User();
        user1.setName("Ekaterina Ivanova");
        user1.setEmail("poPP@mail.test");

        storage.save(user1);

        Optional<User> foundUser = storage.findByEmailLike("poPP@mail.test");

        assertThat(foundUser.isPresent(), equalTo(true));
        assertThat(foundUser.get(), equalTo(user1));
    }

    @Test
    public void findByEmailLikeAndIdNotTest() {
        User user1 = new User();
        user1.setName("Ekaterina Ivanova");
        user1.setEmail("poPP@mail.test");

        storage.save(user1);

        User user2 = new User();
        user2.setName("Matthew McConaughey");
        user2.setEmail("ghhGIYGn@mail.test");

        storage.save(user2);

        Optional<User> foundUser = storage.findByEmailLikeAndIdNot("poPP@mail.test", user2.getId());
        assertTrue(foundUser.isPresent());

        Optional<User> userNotFound = storage.findByEmailLikeAndIdNot("ghhGIYGn@mail.test", user2.getId());
        assertFalse(userNotFound.isPresent());
    }
}