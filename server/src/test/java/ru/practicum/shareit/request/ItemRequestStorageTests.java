package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRequestStorageTests {

    @Autowired
    ItemRequestStorage itemRequestStorage;
    @Autowired
    UserStorage userStorage;

    @Test
    public void findByRequesterIdOrderByCreatedTest() {
        User requester = createRequester("kjkjk@ww.test");
        Request request1 = createRequest(requester);
        Request request2 = createRequest(requester);

        List<Request> requestsFound = itemRequestStorage.findByRequesterIdOrderByCreated(requester.getId());

        assertThat(request1, equalTo(requestsFound.getFirst()));
        assertThat(request2, equalTo(requestsFound.getLast()));
        assertThat(requestsFound.size(), equalTo(2));
    }

    private User createRequester(String email) {
        User requester = new User();
        requester.setName("Kokoko");
        requester.setEmail(email);
        userStorage.save(requester);

        return requester;
    }

    private Request createRequest(User requester) {
        Request request = new Request();
        request.setDescription("Kokoko was here");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        itemRequestStorage.save(request);

        return request;
    }
}
