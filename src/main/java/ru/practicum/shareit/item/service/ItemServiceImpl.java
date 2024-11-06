package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ModelMapper modelMapper;
    @Qualifier("InMemoryItems")
    private final ItemStorage itemStorage;
    @Qualifier("InMemoryUsers")
    private final UserStorage userStorage;

    @Override
    public ItemDto addNewItem(CreateItemRequest request, long userId) {
        Item item = modelMapper.map(request, Item.class);

        if (!userStorage.contains(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        item.setOwner(userId);
        return modelMapper.map(itemStorage.addNewItem(item), ItemDto.class);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequest request, long itemId, long userId) {
        if (!itemStorage.contains(itemId)) {
            throw new NotFoundException("Запрашиваемая вещь не была найдена");
        }

        if (userId != itemStorage.getItemById(itemId).getOwner()) {
            throw new ForbiddenException("Внесение изменений доступно только владельцу");
        }
        Item item = modelMapper.map(request, Item.class);
        item.setId(itemId);
        item.setOwner(userId);

        return modelMapper.map(itemStorage.updateItem(item), ItemDto.class);
    }

    @Override
    public ItemDto getItemDtoById(Long itemId) {
        if (!itemStorage.contains(itemId)) {
            throw new NotFoundException("Запрашиваемая вещь не была найдена");
        }
        Item item = itemStorage.getItemById(itemId);

        return modelMapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItemsDtoOfUser(long ownerId) {
        List<Item> items = itemStorage.getAllItemsOfUser(ownerId);
        if (items.isEmpty()) {
            throw new NotFoundException("По данному запросу не удалось что-либо найти :(");
        }

        List<ItemDto> dtos = new ArrayList<>();

        for (Item item : items) {
            dtos.add(modelMapper.map(item, ItemDto.class));
        }

        return dtos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return List.of();
        }

        List<Item> itemsFound = itemStorage.searchItem(text.toUpperCase());
        if (itemsFound.isEmpty()) {
            throw new NotFoundException("По данному запросу не удалось что-либо найти :(");
        }

        List<ItemDto> dtosFound = new ArrayList<>();
        for (Item item : itemsFound) {
            if (item.getAvailable()) {
                dtosFound.add(modelMapper.map(item, ItemDto.class));
            }
        }

        return dtosFound;
    }
}
