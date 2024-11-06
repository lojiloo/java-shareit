package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(CreateItemRequest request, long userId);

    ItemDto updateItem(UpdateItemRequest request, long itemId, long userId);

    ItemDto getItemDtoById(Long itemId);

    List<ItemDto> getAllItemsDtoOfUser(long ownerId);

    List<ItemDto> searchItem(String text);

}
