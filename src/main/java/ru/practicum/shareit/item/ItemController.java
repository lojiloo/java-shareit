package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
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
    public Item addNewItem(@RequestBody @Valid CreateItemRequest request,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.addNewItem(request, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody @Valid UpdateItemRequest request,
                           @PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable Long itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsDtoOfUser(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllItemsDtoOfUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
