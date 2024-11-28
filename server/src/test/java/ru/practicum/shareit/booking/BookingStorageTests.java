package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingStorageTests {

    @Autowired
    BookingStorage bookingStorage;
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;

    @Test
    public void findByBookerIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        Booking bookingFound = bookingStorage.findByBookerId(bookingCreated.getBooker().getId()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findByItemIdsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        Booking bookingFound = bookingStorage.findByItemIds(List.of(bookingCreated.getItem().getId())).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void existsItemIdByBookerIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        assertTrue(bookingStorage.existsItemIdByBookerId(bookingCreated.getBooker().getId()));
    }

    @Test
    public void findEndOfBookingByUserIdAndItemIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        long itemId = bookingCreated.getItem().getId();
        long bookerId = bookingCreated.getBooker().getId();

        LocalDate dateCreated = bookingCreated.getEnd().toLocalDate();
        LocalDate dateFound = bookingStorage.findEndOfBookingByUserIdAndItemId(bookerId, itemId).toLocalDate();

        assertEquals(dateCreated, dateFound);
    }

    @Test
    public void findCurrentByBookerIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().minusDays(11));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        long bookerId = bookingCreated.getBooker().getId();

        Booking bookingFound = bookingStorage.findCurrentByBookerId(bookerId, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findCurrentByItemIdsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().minusDays(11));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        Booking bookingFound = bookingStorage.findCurrentByItemIds(itemIds, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findPastByBookerIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().minusDays(2));
        bookingCreated.setEnd(LocalDateTime.now().minusDays(1));
        bookingStorage.save(bookingCreated);

        long bookerId = bookingCreated.getBooker().getId();

        Booking bookingFound = bookingStorage.findPastByBookerId(bookerId, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findPastByItemIdsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().minusDays(2));
        bookingCreated.setEnd(LocalDateTime.now().minusDays(1));
        bookingStorage.save(bookingCreated);

        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        Booking bookingFound = bookingStorage.findPastByItemIds(itemIds, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findFutureByBookerIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        long bookerId = bookingCreated.getBooker().getId();

        Booking bookingFound = bookingStorage.findFutureByBookerId(bookerId, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findFutureByItemIdsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        Booking bookingFound = bookingStorage.findFutureByItemIds(itemIds, LocalDateTime.now()).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findByStatusEqualsAndBookerIdEqualsOrderByStartDescTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        Status status = bookingCreated.getStatus();
        long bookerId = bookingCreated.getBooker().getId();

        Booking bookingsFound = bookingStorage.findByStatusEqualsAndBookerIdEqualsOrderByStartDesc(status, bookerId).getFirst();

        assertEquals(bookingsFound, bookingCreated);
    }

    @Test
    public void findByStatusEqualsAndItemIdInOrderByStartDescTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        Status status = bookingCreated.getStatus();
        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        Booking bookingFound = bookingStorage.findByStatusEqualsAndItemIdInOrderByStartDesc(status, itemIds).getFirst();

        assertEquals(bookingCreated, bookingFound);
    }

    @Test
    public void findItemIdByBookingIdTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        long itemIdCreated = bookingCreated.getItem().getId();
        long bookingId = bookingCreated.getId();

        long itemIdFound = bookingStorage.findItemIdByBookingId(bookingId);

        assertEquals(itemIdCreated, itemIdFound);
    }

    @Test
    public void findNextBookingsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().plusDays(10));
        bookingCreated.setEnd(LocalDateTime.now().plusDays(11));
        bookingStorage.save(bookingCreated);

        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        List<Booking> nextBookings = bookingStorage.findNextBookings(itemIds, LocalDateTime.now());

        assertEquals(nextBookings.getFirst(), bookingCreated);
    }

    @Test
    public void findLastBookingsTest() {
        Booking bookingCreated = createBooking();
        bookingCreated.setStart(LocalDateTime.now().minusDays(2));
        bookingCreated.setEnd(LocalDateTime.now().minusDays(1));
        bookingStorage.save(bookingCreated);

        List<Long> itemIds = new ArrayList<>();
        itemIds.add(bookingCreated.getItem().getId());

        List<Booking> lastBookings = bookingStorage.findLastBookings(itemIds, LocalDateTime.now());

        assertEquals(lastBookings.getFirst(), bookingCreated);
    }


    private Booking createBooking() {
        User user = new User();
        user.setName("lojilo");
        user.setEmail("gghg@ddad.test");
        userStorage.save(user);

        Item item = new Item();
        item.setName("My precious");
        item.setDescription("GIYDGgfiyfgsyfgYGYUyg");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemStorage.save(item);

        Booking bookingCreated = new Booking();
        bookingCreated.setItem(item);
        bookingCreated.setBooker(user);
        bookingCreated.setStatus(Status.APPROVED);

        return bookingCreated;
    }
}
