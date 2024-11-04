package ru.practicum.shareit.item.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("InMemoryItems")
@Data
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private Map<Long, Item> items = new HashMap<>();
    private long itemId = 0L;

    @Override
    public Item addNewItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long id = item.getId();

        if (item.getName() == null) item.setName(items.get(id).getName());
        if (item.getDescription() == null) item.setDescription(items.get(id).getDescription());
        if (item.getAvailable() == null) item.setAvailable(items.get(id).getAvailable());

        items.put(id, item);

        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemsOfUser(long ownerId) {
        List<Item> userItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner() == ownerId) {
                userItems.add(item);
            }
        }

        return userItems;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> searchResults = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getName().toUpperCase().contains(text)
                    || item.getDescription().toUpperCase().contains(text)) {
                searchResults.add(item);
            }
        }

        return searchResults;
    }

    @Override
    public boolean contains(Item item) {
        return items.containsValue(item);
    }

    @Override
    public boolean contains(long itemId) {
        return items.containsKey(itemId);
    }

    private long getNextId() {
        return ++itemId;
    }
}
