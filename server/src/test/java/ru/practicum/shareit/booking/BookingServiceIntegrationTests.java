package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTests {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void addNewBookingTest() {
        UserDto user = userService.createUser(createUser("jjjj@www.test"));
        ItemDto item = itemService.addNewItem(createItem("Test item"), user.getId());

        BookingDto booking = bookingService.addNewBooking(createBooking(item.getId()), user.getId());

        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getItem(), equalTo(item));
    }

    @Test
    public void updateBookingStatus() {
        UserDto user = userService.createUser(createUser("jjjj@www.test"));
        ItemDto item = itemService.addNewItem(createItem("Test item"), user.getId());

        BookingDto booking = bookingService.addNewBooking(createBooking(item.getId()), user.getId());

        bookingService.updateBookingStatus(booking.getId(), true, user.getId());
        assertEquals(Status.APPROVED, bookingService.getBookingInfoById(item.getId(), user.getId()).getStatus());

        bookingService.updateBookingStatus(booking.getId(), false, user.getId());
        assertEquals(Status.REJECTED, bookingService.getBookingInfoById(item.getId(), user.getId()).getStatus());
    }

    @Test
    public void getBookingInfoByIdTest() {
        UserDto owner = userService.createUser(createUser("yyy@www.test"));
        ItemDto item = itemService.addNewItem(createItem("Test item"), owner.getId());

        BookingDto booking = bookingService.addNewBooking(createBooking(item.getId()), owner.getId());
        BookingDto bookingFound = bookingService.getBookingInfoById(booking.getId(), owner.getId());

        assertThat(booking, equalTo(bookingFound));
    }

    @Test
    public void findByStateForBookerTestPast() {
        UserDto booker = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemPast = itemService.addNewItem(createItem("PAST"), booker.getId());

        CreateBookingDto requestPast = createBooking(itemPast.getId());
        requestPast.setStart(LocalDateTime.now().minusDays(11));
        requestPast.setEnd(LocalDateTime.now().minusDays(10));
        bookingService.addNewBooking(requestPast, booker.getId());

        BookingDto bookingPast = bookingService.findByStateForBooker(State.PAST, booker.getId()).getFirst();
        assertThat(requestPast.getItemId(), equalTo(bookingPast.getItemId()));
    }

    @Test
    public void findByStateForBookerTestCurrent() {
        UserDto booker = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemCurrent = itemService.addNewItem(createItem("CURRENT"), booker.getId());

        CreateBookingDto requestCurrent = createBooking(itemCurrent.getId());
        requestCurrent.setStart(LocalDateTime.now().minusDays(11));
        requestCurrent.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.addNewBooking(requestCurrent, booker.getId());

        BookingDto bookingCurrent = bookingService.findByStateForBooker(State.CURRENT, booker.getId()).getFirst();
        assertThat(requestCurrent.getItemId(), equalTo(bookingCurrent.getItemId()));
    }

    @Test
    public void findByStateForBookerTestFuture() {
        UserDto booker = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemFuture = itemService.addNewItem(createItem("FUTURE"), booker.getId());

        CreateBookingDto requestFuture = createBooking(itemFuture.getId());
        requestFuture.setStart(LocalDateTime.now().plusDays(10));
        requestFuture.setEnd(LocalDateTime.now().plusDays(11));
        bookingService.addNewBooking(requestFuture, booker.getId());

        BookingDto bookingFuture = bookingService.findByStateForBooker(State.FUTURE, booker.getId()).getFirst();
        assertThat(requestFuture.getItemId(), equalTo(bookingFuture.getItemId()));
    }

    @Test
    public void findByStateForBookerTestWaiting() {
        UserDto booker = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemWaiting = itemService.addNewItem(createItem("WAITING"), booker.getId());

        CreateBookingDto requestWaiting = createBooking(itemWaiting.getId());
        bookingService.addNewBooking(requestWaiting, booker.getId());

        BookingDto bookingWaiting = bookingService.findByStateForBooker(State.WAITING, booker.getId()).getFirst();
        assertThat(requestWaiting.getItemId(), equalTo(bookingWaiting.getItemId()));
    }

    @Test
    public void findByStateForBookerTestRejected() {
        UserDto booker = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemRejected = itemService.addNewItem(createItem("REJECTED"), booker.getId());

        CreateBookingDto requestRejected = createBooking(itemRejected.getId());
        BookingDto dto = bookingService.addNewBooking(requestRejected, booker.getId());
        bookingService.updateBookingStatus(dto.getId(), false, booker.getId());

        BookingDto bookingRejected = bookingService.findByStateForBooker(State.REJECTED, booker.getId()).getFirst();
        assertThat(requestRejected.getItemId(), equalTo(bookingRejected.getItemId()));
    }

    @Test
    public void findByStateForOwnerTestPast() {
        UserDto owner = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemPast = itemService.addNewItem(createItem("PAST"), owner.getId());

        CreateBookingDto requestPast = createBooking(itemPast.getId());
        requestPast.setStart(LocalDateTime.now().minusDays(11));
        requestPast.setEnd(LocalDateTime.now().minusDays(10));
        bookingService.addNewBooking(requestPast, owner.getId());

        BookingDto bookingPast = bookingService.findByStateForOwner(State.PAST, owner.getId()).getFirst();
        assertThat(requestPast.getItemId(), equalTo(bookingPast.getItemId()));
    }

    @Test
    public void findByStateForOwnerTestCurrent() {
        UserDto owner = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemCurrent = itemService.addNewItem(createItem("CURRENT"), owner.getId());

        CreateBookingDto requestCurrent = createBooking(itemCurrent.getId());
        requestCurrent.setStart(LocalDateTime.now().minusDays(11));
        requestCurrent.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.addNewBooking(requestCurrent, owner.getId());

        BookingDto bookingCurrent = bookingService.findByStateForOwner(State.CURRENT, owner.getId()).getFirst();
        assertThat(requestCurrent.getItemId(), equalTo(bookingCurrent.getItemId()));
    }

    @Test
    public void findByStateForOwnerTestFuture() {
        UserDto owner = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemFuture = itemService.addNewItem(createItem("FUTURE"), owner.getId());

        CreateBookingDto requestFuture = createBooking(itemFuture.getId());
        requestFuture.setStart(LocalDateTime.now().plusDays(10));
        requestFuture.setEnd(LocalDateTime.now().plusDays(11));
        bookingService.addNewBooking(requestFuture, owner.getId());

        BookingDto bookingFuture = bookingService.findByStateForOwner(State.FUTURE, owner.getId()).getFirst();
        assertThat(requestFuture.getItemId(), equalTo(bookingFuture.getItemId()));
    }

    @Test
    public void findByStateForOwnerTestWaiting() {
        UserDto owner = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemWaiting = itemService.addNewItem(createItem("WAITING"), owner.getId());

        CreateBookingDto requestWaiting = createBooking(itemWaiting.getId());
        bookingService.addNewBooking(requestWaiting, owner.getId());

        BookingDto bookingWaiting = bookingService.findByStateForOwner(State.WAITING, owner.getId()).getFirst();
        assertThat(requestWaiting.getItemId(), equalTo(bookingWaiting.getItemId()));
    }

    @Test
    public void findByStateForOwnerTestRejected() {
        UserDto owner = userService.createUser(createUser("gjhjh@ww.test"));
        ItemDto itemRejected = itemService.addNewItem(createItem("REJECTED"), owner.getId());

        CreateBookingDto requestRejected = createBooking(itemRejected.getId());
        BookingDto dto = bookingService.addNewBooking(requestRejected, owner.getId());
        bookingService.updateBookingStatus(dto.getId(), false, owner.getId());

        BookingDto bookingRejected = bookingService.findByStateForOwner(State.REJECTED, owner.getId()).getFirst();
        assertThat(requestRejected.getItemId(), equalTo(bookingRejected.getItemId()));
    }


    private CreateUserDto createUser(String email) {
        CreateUserDto request = new CreateUserDto();
        request.setName("lojilo");
        request.setEmail(email);

        return request;
    }

    private CreateBookingDto createBooking(long itemId) {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(10));
        request.setEnd(LocalDateTime.now().plusDays(11));

        return request;
    }

    private CreateItemDto createItem(String name) {
        CreateItemDto request = new CreateItemDto();
        request.setName(name);
        request.setDescription("Really delicious");
        request.setAvailable(true);

        return request;
    }
}
