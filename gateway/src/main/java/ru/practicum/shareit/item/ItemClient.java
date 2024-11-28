package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.dto.comment.CreateComment;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addNewItem(CreateItem request, long ownerId) {
        return post("", ownerId, request);
    }

    public ResponseEntity<Object> addNewComment(CreateComment request, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, request);
    }

    public ResponseEntity<Object> updateItem(UpdateItem request, long itemId, long userId) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getAllItemsOfUser(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItem(String text) {
        Map<String, Object> requestParam = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, requestParam);
    }
}
