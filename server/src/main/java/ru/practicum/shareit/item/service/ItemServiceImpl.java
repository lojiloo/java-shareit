package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ModelMapper modelMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public ItemDto addNewItem(CreateItemDto request, long userId) {
        Item item = modelMapper.map(request, Item.class);

        if (request.getName().isBlank()) {
            throw new BadRequestException("Название вещи не может быть пустым");
        }

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        item.setOwner(userId);
        return modelMapper.map(itemStorage.save(item), ItemDto.class);
    }

    @Override
    public CommentDto addNewComment(CreateCommentDto request, long itemId, long userId) {
        if (!bookingStorage.existsItemIdByBookerId(userId)) {
            throw new BadRequestException("Оставить комментарий можно на ранее арендованную вещь");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!bookingStorage.findEndOfBookingByUserIdAndItemId(userId, itemId).isBefore(now)) {
            throw new BadRequestException("Оставить комментарий можно после завершения аренды");
        }

        Comment comment = modelMapper.map(request, Comment.class);
        comment.setAuthor(userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с таким id не найден")
        ));
        comment.setItem(itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Запрашиваемая вещь не была найдена")
        ));
        comment.setCreated(LocalDateTime.now());

        return modelMapper.map(commentStorage.save(comment), CommentDto.class);
    }

    @Override
    public ItemDto updateItem(UpdateItemDto request, long itemId, long userId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("Запрашиваемая вещь не была найдена");
        }
        if (userId != itemStorage.getReferenceById(itemId).getOwner()) {
            throw new ForbiddenException("Внесение изменений доступно только владельцу");
        }

        Item item = itemStorage.getReferenceById(itemId);
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }

        return modelMapper.map(itemStorage.save(item), ItemDto.class);
    }

    @Override
    public ItemDtoWithBookings getItemById(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Запрашиваемая вещь не была найдена")
        );

        ItemDtoWithBookings dtoWithBookings = modelMapper.map(item, ItemDtoWithBookings.class);

        List<Comment> comments = commentStorage.findByItemId(itemId);
        if (!comments.isEmpty()) {
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment c : comments) {
                commentDtos.add(modelMapper.map(c, CommentDto.class));
            }
            dtoWithBookings.setComments(commentDtos);
        }

        return dtoWithBookings;
    }

    @Override
    public List<ItemDtoWithBookings> getAllItemsOfUser(long ownerId) {
        List<Item> items = itemStorage.findByOwner(ownerId);
        if (items.isEmpty()) {
            throw new NotFoundException("У вас ещё не создано ни одного предложения для аренды");
        }

        List<Long> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> lastBookings = bookingStorage.findLastBookings(itemIds, now)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Booking>> nextBookings = bookingStorage.findNextBookings(itemIds, now)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> comments = commentStorage.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemDtoWithBookings> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(modelMapper.map(item, ItemDtoWithBookings.class));
        }

        for (ItemDtoWithBookings dto : dtos) {
            long id = dto.getId();

            if (lastBookings.containsKey(id)) {
                Booking lastBooking = lastBookings.get(id).getFirst();
                dto.setLastBooking(modelMapper.map(lastBooking, BookingDto.class));
            }

            if (nextBookings.containsKey(id)) {
                Booking nextBooking = nextBookings.get(id).getFirst();
                dto.setNextBooking(modelMapper.map(nextBooking, BookingDto.class));
            }

            if (comments.containsKey(id)) {
                List<CommentDto> itemComments = comments.get(id)
                        .stream()
                        .map(comment -> modelMapper.map(comment, CommentDto.class))
                        .toList();
                dto.setComments(itemComments);
            }
        }

        return dtos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> itemsFound = itemStorage.findByNameOrDescriptionContainingIgnoreCaseAndIsAvailable(text);

        return itemsFound.stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
