package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemStorageTests {
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    ItemRequestStorage requestStorage;
    ModelMapper mapper = new ModelMapper();

    @Test
    public void findItemIdByOwnerIdTest() {
        User user = createUser();
        userStorage.save(user);

        Item item = createItem();
        item.setOwner(user.getId());
        itemStorage.save(item);

        List<Long> itemRetrieved = itemStorage.findItemIdByOwnerId(user.getId());
        long itemId = itemRetrieved.getFirst();

        assertThat(itemId, equalTo(item.getId()));
    }

    @Test
    public void findOwnerIdByItemIdTest() {
        User user = createUser();
        userStorage.save(user);

        Item item = createItem();
        item.setOwner(user.getId());
        itemStorage.save(item);

        long ownerId = itemStorage.findOwnerIdByItemId(item.getId());

        assertThat(ownerId, equalTo(user.getId()));
    }

    @Test
    public void findByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTest() {
        User user = createUser();
        userStorage.save(user);

        Item item = createItem();
        item.setOwner(user.getId());
        itemStorage.save(item);

        String text = "banana";
        Item itemRetrieved = itemStorage.findByNameOrDescriptionContainingIgnoreCaseAndIsAvailable(text).getFirst();

        assertThat(itemRetrieved.getName(), equalTo(item.getName()));
    }

    @Test
    public void findByOwnerTest() {
        User user = createUser();
        userStorage.save(user);

        Item item = createItem();
        item.setOwner(user.getId());
        itemStorage.save(item);

        Item itemRetrieved = itemStorage.findByOwner(user.getId()).getFirst();

        assertThat(item.getName(), equalTo(itemRetrieved.getName()));
    }

    @Test
    public void findByRequestIdTest() {
        User user = createUser();
        userStorage.save(user);

        Request request = createRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        requestStorage.save(request);

        Item item = createItem();
        item.setOwner(user.getId());
        item.setRequestId(request.getId());
        itemStorage.save(item);

        Item itemRetrieved = itemStorage.findByRequestId(request.getId()).getFirst();

        assertThat(item.getName(), equalTo(itemRetrieved.getName()));
    }

    private User createUser() {
        User user = new User();
        user.setName("Baobab");
        user.setEmail("tdysr5@ff.bu");

        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setName("Banana");
        item.setDescription("fgsjDF trfguy GGge");
        item.setAvailable(true);

        return item;
    }

    private Request createRequest() {
        Request request = new Request();
        request.setDescription("pleaaaaaaseeee");

        return request;
    }
}