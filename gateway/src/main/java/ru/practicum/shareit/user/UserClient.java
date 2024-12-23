package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Component
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    ResponseEntity<Object> createUser(CreateUserDto request) {
        return post("", request);
    }

    ResponseEntity<Object> updateUser(UpdateUserDto request, long userId) {
        return patch("/" + userId, request);
    }

    ResponseEntity<Object> getUserById(long userId) {
        return get("/" + userId);
    }

    ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    ResponseEntity<Object> deleteUser(long userId) {
        return delete("/" + userId);
    }
}
