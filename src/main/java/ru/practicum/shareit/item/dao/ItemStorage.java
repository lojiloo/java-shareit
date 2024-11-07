package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addNewItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getAllItemsOfUser(long ownerId);

    List<Item> searchItem(String text);

    boolean contains(Item item);

    boolean contains(long itemId);
}
