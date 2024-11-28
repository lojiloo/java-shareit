package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.dto.comment.CreateComment;

import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestBody @Valid CreateItem request,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Поступил запрос на добавление новой вещи от пользователя с id={}", ownerId);
        return itemClient.addNewItem(request, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestBody @Valid CreateComment request,
                                                @PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос от пользователя с id={} на добавление нового комментария на вещь с id={}", userId, itemId);
        return itemClient.addNewComment(request, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid UpdateItem request,
                                             @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос от пользователя с id={} на обновление информации о вещи с id={}", userId, itemId);
        return itemClient.updateItem(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        log.info("Поступил запрос на получение информации о вещи с id={}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Поступил запрос на предоставление информации о вещах, созданных пользователем с id={}", ownerId);
        return itemClient.getAllItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        if (text.isEmpty() || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Поступил запрос на поиск вещи по критерию={}", text);
        return itemClient.searchItem(text);
    }
}
