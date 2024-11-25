package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(CreateItemDto request, long userId);

    CommentDto addNewComment(CreateCommentDto request, long itemId, long userId);

    ItemDto updateItem(UpdateItemDto request, long itemId, long userId);

    ItemDtoWithBookings getItemById(Long itemId);

    List<ItemDtoWithBookings> getAllItemsOfUser(long ownerId);

    List<ItemDto> searchItem(String text);

}
