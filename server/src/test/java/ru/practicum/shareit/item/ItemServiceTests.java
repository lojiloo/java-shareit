package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dao.BookingStorage;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    ItemStorage itemStorageMock;
    @Mock
    UserStorage userStorageMock;
    @Mock
    BookingStorage bookingStorageMock;
    @Mock
    CommentStorage commentStorageMock;
    private ItemService service;
    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        service = new ItemServiceImpl(mapper,
                itemStorageMock,
                userStorageMock,
                bookingStorageMock,
                commentStorageMock);
    }

    @Test
    public void addNewItemTest() {
        CreateItemDto request = createItem("Blue Apple");
        Item item = mapper.map(request, Item.class);

        when(userStorageMock.existsById(anyLong()))
                .thenReturn(true);
        when(itemStorageMock.save(any(Item.class)))
                .thenReturn(item);

        ItemDto dto = service.addNewItem(request, 1L);

        assertThat(dto, equalTo(mapper.map(item, ItemDto.class)));

        verify(userStorageMock, times(1))
                .existsById(anyLong());
        verify(itemStorageMock, times(1))
                .save(any(Item.class));
        verifyNoMoreInteractions(userStorageMock, itemStorageMock);
    }

    @Test
    public void addNewItemWithNoNameTest() {
        CreateItemDto request = createItem("");

        assertThrows(BadRequestException.class, () -> service.addNewItem(request, 1L));
    }

    @Test
    public void addNewItemWithNonexistentUserIdTest() {
        CreateItemDto request = createItem("Blue Apple");

        when(userStorageMock.existsById(10000L))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.addNewItem(request, 10000L));
    }

    @Test
    public void addNewCommentTest() {
        CreateCommentDto request = createComment("Good one");
        Comment comment = mapper.map(request, Comment.class);

        Item item = mapper.map(createItem("Blue Apple"), Item.class);
        comment.setItem(item);
        User user = mapper.map(createUser("j@df.test"), User.class);
        comment.setAuthor(user);

        when(bookingStorageMock.existsItemIdByBookerId(anyLong()))
                .thenReturn(true);

        when(bookingStorageMock.findEndOfBookingByUserIdAndItemId(anyLong(), anyLong()))
                .thenReturn(LocalDateTime.now().minusDays(1));

        when(userStorageMock.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemStorageMock.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentStorageMock.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto dto = service.addNewComment(request, 1L, 1L);

        assertThat(dto, equalTo(mapper.map(comment, CommentDto.class)));

        verify(commentStorageMock, times(1))
                .save(any(Comment.class));
        verifyNoMoreInteractions(bookingStorageMock, userStorageMock, itemStorageMock, commentStorageMock);
    }

    @Test
    public void addNewCommentWhileBooking() {
        CreateCommentDto request = createComment("Good one");

        when(bookingStorageMock.existsItemIdByBookerId(anyLong()))
                .thenReturn(true);
        when(bookingStorageMock.findEndOfBookingByUserIdAndItemId(anyLong(), anyLong()))
                .thenReturn(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class,
                () -> service.addNewComment(request, 1L, 1L));
    }

    @Test
    public void updateNonexistentItemTest() {
        UpdateItemDto request = new UpdateItemDto();
        request.setAvailable(false);

        when(itemStorageMock.existsById(100000L))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.updateItem(request, 100000L, 1L));
    }

    @Test
    public void updateItemWithNonexistentUserIdTest() {
        UpdateItemDto request = new UpdateItemDto();
        request.setAvailable(false);
        Item item = mapper.map(createItem("Blue Apple"), Item.class);
        item.setOwner(100000L);

        when(itemStorageMock.existsById(anyLong()))
                .thenReturn(true);
        when(itemStorageMock.getReferenceById(100000L))
                .thenReturn(item);

        assertThrows(ForbiddenException.class, () -> service.updateItem(request, 100000L, 1L));
    }

    @Test
    public void getItemByIdWithNoCommentsTest() {
        Item item = mapper.map(createItem("Blue Apple"), Item.class);

        when(itemStorageMock.findById(1L))
                .thenReturn(Optional.of(item));

        ItemDtoWithBookings dto = service.getItemById(1L);

        assertThat(dto, equalTo(mapper.map(item, ItemDtoWithBookings.class)));

        verify(itemStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(itemStorageMock);
    }

    @Test
    public void getItemByIdWithCommentsTest() {
        Item item = mapper.map(createItem("Blue Apple"), Item.class);

        when(itemStorageMock.findById(1L))
                .thenReturn(Optional.of(item));
        when(commentStorageMock.findByItemId(1L))
                .thenReturn(List.of(new Comment(), new Comment()));

        ItemDtoWithBookings dto = service.getItemById(1L);

        assertFalse(dto.getComments().isEmpty());

        verify(itemStorageMock, atLeast(1))
                .findById(anyLong());
        verify(commentStorageMock, atLeast(1))
                .findByItemId(anyLong());
        verifyNoMoreInteractions(itemStorageMock, commentStorageMock);
    }

    @Test
    public void searchItemTest() {
        Item item = mapper.map(createItem("Blue Apple"), Item.class);
        String text = "apple";

        when(itemStorageMock.findByNameOrDescriptionContainingIgnoreCaseAndIsAvailable(text))
                .thenReturn(List.of(item));

        ItemDto foundDto = service.searchItem(text).getFirst();

        assertThat(foundDto, equalTo(mapper.map(item, ItemDto.class)));

        verify(itemStorageMock, times(1))
                .findByNameOrDescriptionContainingIgnoreCaseAndIsAvailable(text);
        verifyNoMoreInteractions(itemStorageMock);
    }


    private CreateItemDto createItem(String name) {
        CreateItemDto request = new CreateItemDto();
        request.setName(name);
        request.setDescription("Useless but beautiful");
        request.setAvailable(true);

        return request;
    }

    private CreateCommentDto createComment(String text) {
        CreateCommentDto request = new CreateCommentDto();
        request.setText(text);

        return request;
    }

    private CreateUserDto createUser(String email) {
        CreateUserDto request = new CreateUserDto();
        request.setName("Billy Milligan");
        request.setEmail(email);

        return request;
    }

}
