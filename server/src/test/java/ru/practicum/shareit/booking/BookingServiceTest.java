package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    UserStorage userStorageMock;
    @Mock
    ItemStorage itemStorageMock;
    @Mock
    BookingStorage bookingStorageMock;
    private BookingService service;
    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        service = new BookingServiceImpl(mapper,
                userStorageMock,
                itemStorageMock,
                bookingStorageMock);
    }

    @Test
    public void addNewBookingWithNonexistentUserIdTest() {
        when(userStorageMock.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addNewBooking(createRequest(1L), 1L));

        verify(userStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void addNewBookingWithNonexistentItemIdTest() {
        User user = createUser(1L);

        when(userStorageMock.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemStorageMock.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addNewBooking(createRequest(1L), 1L));

        verify(userStorageMock, times(1))
                .findById(anyLong());
        verify(itemStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userStorageMock, itemStorageMock);
    }

    @Test
    public void addNewBookingWithItemNoAvailableTest() {
        User user = createUser(1L);
        Item item = createItem(1L, 1L);
        item.setAvailable(false);

        when(userStorageMock.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemStorageMock.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> service.addNewBooking(createRequest(item.getId()), user.getId()));

        verify(userStorageMock, times(1))
                .findById(anyLong());
        verify(itemStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userStorageMock, itemStorageMock);
    }

    @Test
    public void updateBookingWithNonexistentBookingId() {
        when(bookingStorageMock.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateBookingStatus(1L, true, 1L));

        verify(bookingStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(bookingStorageMock);
    }

    @Test
    public void updateBookingButUserIsNotAnOwner() {
        User user = createUser(1L);
        Item item = createItem(1L, 1L);
        Booking booking = createBooking(user, item, 1L);

        when(bookingStorageMock.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> service.updateBookingStatus(booking.getId(), true, 2L));

        verify(bookingStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(bookingStorageMock);
    }

    @Test
    public void getBookingInfoByIdWithNonexistentUserTest() {
        when(userStorageMock.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getBookingInfoById(1L, 1L));

        verify(userStorageMock, times(1))
                .existsById(anyLong());
        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void getBookingInfoByIdWithNonexistentBookingTest() {
        User user = createUser(1L);

        when(userStorageMock.existsById(user.getId()))
                .thenReturn(true);
        when(bookingStorageMock.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getBookingInfoById(1L, user.getId()));

        verify(userStorageMock, times(1))
                .existsById(user.getId());
        verify(bookingStorageMock, times(1))
                .existsById(anyLong());
        verifyNoMoreInteractions(userStorageMock, bookingStorageMock);
    }

    @Test
    public void getBookingInfoByIdByStrangerTest() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        User user = createUser(3L);

        Item item = createItem(1L, 2L);
        Booking booking = createBooking(booker, item, 1L);

        when(userStorageMock.existsById(user.getId()))
                .thenReturn(true);
        when(bookingStorageMock.existsById(booking.getId()))
                .thenReturn(true);

        when(bookingStorageMock.findItemIdByBookingId(booking.getId()))
                .thenReturn(item.getId());
        when(itemStorageMock.findOwnerIdByItemId(item.getId()))
                .thenReturn(owner.getId());
        when(bookingStorageMock.getReferenceById(booking.getId()))
                .thenReturn(booking);

        assertThrows(ForbiddenException.class, () -> service.getBookingInfoById(booking.getId(), user.getId()));

        verifyNoMoreInteractions(userStorageMock, itemStorageMock, bookingStorageMock);
    }

    @Test
    public void findByStateForBookerWithNonexistentBookerTest() {
        when(userStorageMock.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.findByStateForBooker(State.PAST, 1L));

        verify(userStorageMock, times(1))
                .existsById(anyLong());
        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void findByStateForOwnerWithNoItemsSuggestedTest() {
        User user = createUser(1L);

        when(itemStorageMock.findItemIdByOwnerId(user.getId()))
                .thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> service.findByStateForOwner(State.PAST, user.getId()));

        verify(itemStorageMock, times(1))
                .findItemIdByOwnerId(user.getId());
        verifyNoMoreInteractions(itemStorageMock);
    }


    private User createUser(long id) {
        User user = new User();
        user.setName("lojilo");
        user.setEmail("gjhgj@ee.test");

        user.setId(id);

        return user;
    }

    private Item createItem(long itemId, long ownerId) {
        Item item = new Item();
        item.setName("Nyam-nyam-nyam");
        item.setDescription("Really delicious");
        item.setAvailable(true);

        item.setOwner(ownerId);
        item.setId(itemId);

        return item;
    }

    private Booking createBooking(User user, Item item, long id) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        booking.setId(id);

        return booking;
    }

    private CreateBookingDto createRequest(long itemId) {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(10));
        request.setEnd(LocalDateTime.now().plusDays(11));

        return request;
    }
}
