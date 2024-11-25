package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;

import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody CreateRequestDto request,
                                                @RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.info("Поступил запрос на создание заказа от пользователя с id={}", requesterId);
        return requestClient.createRequest(requesterId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос от пользователя с id={} на предоставление информации о всех его заказах", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
        log.info("Поступил запрос на предоставление информации о заказе с id={}", requestId);
        return requestClient.getRequestById(requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        log.info("Поступил запрос на получение информации обо всех открытых заказах");
        return requestClient.getAllRequests();
    }

}
