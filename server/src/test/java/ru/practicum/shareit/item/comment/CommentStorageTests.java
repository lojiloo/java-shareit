package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class CommentStorageTests {

    @Autowired
    CommentStorage commentStorage;
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;

    @Test
    public void findByItemIdTest() {
        Comment commentCreated = createComment("COOOOOOOOOL");
        commentStorage.save(commentCreated);

        long itemId = commentCreated.getItem().getId();

        Comment commentFound = commentStorage.findByItemId(itemId).getFirst();

        assertThat(commentCreated, equalTo(commentFound));
    }

    @Test
    public void findByItemIdInTest() {
        Comment commentCreated1 = createComment("COOOOOOOOOL");
        commentStorage.save(commentCreated1);

        Comment commentCreated2 = createComment("boring");
        commentStorage.save(commentCreated2);

        Comment commentCreated3 = createComment("my child doesn't understand guys");
        commentStorage.save(commentCreated3);

        long itemId1 = commentCreated1.getItem().getId();
        long itemId2 = commentCreated2.getItem().getId();

        List<Comment> commentsFound = commentStorage.findByItemIdIn(List.of(itemId1, itemId2));

        assertEquals(2, commentsFound.size());
        assertFalse(commentsFound.contains(commentCreated3));
    }

    private Comment createComment(String text) {
        User author = new User();
        author.setName("Test Masha");
        author.setEmail("gjjh@ee.test");
        userStorage.save(author);

        Item item = new Item();
        item.setName("early 90s stickers");
        item.setDescription("Cool and old");
        item.setAvailable(true);
        item.setOwner(author.getId());
        itemStorage.save(item);

        Comment commentCreated = new Comment();
        commentCreated.setItem(item);
        commentCreated.setAuthor(author);
        commentCreated.setText(text);
        commentCreated.setCreated(LocalDateTime.now());

        return commentCreated;
    }
}
