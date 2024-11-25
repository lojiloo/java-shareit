package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTests {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    public void updateItemTest() {
        UserDto user = userService.createUser(createUser());
        ItemDto dto = itemService.addNewItem(createItemRequest("Knedlik"), user.getId());

        UpdateItemDto request = new UpdateItemDto();
        request.setName("Czech knedlik");

        ItemDto dtoUpd = itemService.updateItem(request, dto.getId(), user.getId());

        assertNotEquals(dtoUpd.getName(), dto.getName());
        assertThat(dtoUpd.getName(), equalTo("Czech knedlik"));
    }

    @Test
    public void getAllItemsOfUserWithNoBookingsNoCommentsTest() {
        UserDto user = userService.createUser(createUser());
        itemService.addNewItem(createItemRequest("Knedlik"), user.getId());
        itemService.addNewItem(createItemRequest("Hrushka"), user.getId());

        List<ItemDtoWithBookings> dtoNoBookingsNoComments = itemService.getAllItemsOfUser(user.getId());
        String name1 = dtoNoBookingsNoComments.getFirst().getName();
        String name2 = dtoNoBookingsNoComments.getLast().getName();

        assertThat(name1, equalTo("Knedlik"));
        assertThat(name2, equalTo("Hrushka"));
        assertThat(dtoNoBookingsNoComments.size(), equalTo(2));

        assertTrue(dtoNoBookingsNoComments.getFirst().getComments().isEmpty());
        assertTrue(dtoNoBookingsNoComments.getLast().getComments().isEmpty());

        assertNull(dtoNoBookingsNoComments.getFirst().getLastBooking());
        assertNull(dtoNoBookingsNoComments.getLast().getLastBooking());

        assertNull(dtoNoBookingsNoComments.getFirst().getNextBooking());
        assertNull(dtoNoBookingsNoComments.getLast().getNextBooking());
    }

    @Test
    public void getAllItemsOfUserWithBookingsTest() {
        UserDto user = userService.createUser(createUser());
        ItemDto item = itemService.addNewItem(createItemRequest("Candy"), user.getId());

        CreateBookingDto nextBookingRequest = createNextBooking(item.getId());
        BookingDto nextBooking = bookingService.addNewBooking(nextBookingRequest, user.getId());

        CreateBookingDto lastBookingRequest = createLastBooking(item.getId());
        BookingDto lastBooking = bookingService.addNewBooking(lastBookingRequest, user.getId());

        List<ItemDtoWithBookings> dtosWithBookings = itemService.getAllItemsOfUser(user.getId());

        assertThat(dtosWithBookings.size(), equalTo(1));
        ItemDtoWithBookings dto = dtosWithBookings.getFirst();
        assertNotNull(dto.getLastBooking());
    }

    @Test
    public void getAllItemsOfUserWithCommentsTest() {
        UserDto user = userService.createUser(createUser());
        ItemDto item = itemService.addNewItem(createItemRequest("Knedlik"), user.getId());

        assertThrows(BadRequestException.class,
                () -> itemService.addNewComment(createComment("super"), item.getId(), user.getId()));

        bookingService.addNewBooking(createLastBooking(item.getId()), user.getId());
        itemService.addNewComment(createComment("super"), item.getId(), user.getId());

        List<ItemDtoWithBookings> dtos = itemService.getAllItemsOfUser(user.getId());
        assertNotNull(dtos);

        assertThat(dtos.getFirst().getComments().getFirst().getText(), equalTo("super"));
    }

    private CreateItemDto createItemRequest(String name) {
        CreateItemDto request = new CreateItemDto();
        request.setName(name);
        request.setDescription("Really delicious");
        request.setAvailable(true);

        return request;
    }

    private CreateUserDto createUser() {
        CreateUserDto request = new CreateUserDto();
        request.setName("Kiki");
        request.setEmail("koko@kuku.test");

        return request;
    }

    private CreateBookingDto createNextBooking(long itemId) {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(10));
        request.setEnd(LocalDateTime.now().plusDays(11));

        return request;
    }

    private CreateBookingDto createLastBooking(long itemId) {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusNanos(100));

        return request;
    }

    private CreateCommentDto createComment(String text) {
        CreateCommentDto request = new CreateCommentDto();
        request.setText(text);

        return request;
    }

}
