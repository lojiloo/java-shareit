package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(CreateItemRequest request, long userId);

    CommentDto addNewComment(CreateCommentRequest request, long itemId, long userId);

    ItemDto updateItem(UpdateItemRequest request, long itemId, long userId);

    ItemDtoWithBookings getItemById(Long itemId);

    List<ItemDtoWithBookings> getAllItemsOfUser(long ownerId);

    List<ItemDto> searchItem(String text);

}
