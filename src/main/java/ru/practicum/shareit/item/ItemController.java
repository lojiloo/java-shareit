package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentRequest;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestBody @Valid CreateItemRequest request,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.addNewItem(request, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestBody CreateCommentRequest request,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addNewComment(request, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Valid UpdateItemRequest request,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
