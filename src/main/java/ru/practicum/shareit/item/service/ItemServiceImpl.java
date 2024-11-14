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
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public ItemDto addNewItem(CreateItemRequest request, long userId) {
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
    public CommentDto addNewComment(CreateCommentRequest request, long itemId, long userId) {
        if (!bookingStorage.findItemIdByBookerId(userId).contains(itemId)) {
            throw new BadRequestException("Оставить комментарий можно на ранее арендованную вещь");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!bookingStorage.findEndOfBookingByUserIdAndItemId(userId, itemId).isBefore(now)) {
            throw new BadRequestException("Оставить комментарий можно после завершения аренды");
        }

        Comment comment = modelMapper.map(request, Comment.class);
        comment.setAuthor(userStorage.getReferenceById(userId));
        comment.setItem(itemStorage.getReferenceById(itemId));
        comment.setCreated(LocalDateTime.now());

        return modelMapper.map(commentStorage.save(comment), CommentDto.class);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequest request, long itemId, long userId) {
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
    public ItemDtoWithBookings getItemDtoById(Long itemId) {
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
    public List<ItemDtoWithBookings> getAllItemsDtoOfUser(long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        List<Item> items = itemStorage.findByOwner(ownerId).orElseThrow(
                () -> new NotFoundException("У вас ещё не создано ни одного предложения для аренды")
        );

        List<ItemDtoWithBookings> dtos = new ArrayList<>();

        for (Item item : items) {
            ItemDtoWithBookings dtoWithBookings = modelMapper.map(item, ItemDtoWithBookings.class);

            List<Booking> lastBookings = bookingStorage.findLastBookings(item.getId(), now);
            if (!lastBookings.isEmpty()) {
                dtoWithBookings.setLastBooking(modelMapper.map(lastBookings.getFirst(), BookingDto.class));
            }

            List<Booking> nextBookings = bookingStorage.findNextBookings(item.getId(), now);
            if (!nextBookings.isEmpty()) {
                dtoWithBookings.setNextBooking(modelMapper.map(nextBookings.getFirst(), BookingDto.class));
            }

            List<Comment> comments = commentStorage.findByItemId(dtoWithBookings.getId());
            if (!comments.isEmpty()) {
                List<CommentDto> commentDtos = new ArrayList<>();
                for (Comment c : comments) {
                    commentDtos.add(modelMapper.map(c, CommentDto.class));
                }
                dtoWithBookings.setComments(commentDtos);
            }

            dtos.add(dtoWithBookings);
        }

        return dtos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return List.of();
        }

        List<Item> itemsFound = itemStorage.findByNameOrDescriptionContainingIgnoreCase(text).orElseThrow(
                () -> new NotFoundException("По данному запросу не удалось что-либо найти :(")
        );

        return itemsFound.stream()
                .filter(Item::getAvailable)
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
