package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTests {

    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void getRequestsByUserTest() {
        UserDto user = userService.createUser(createUser());
        RequestDtoWithItems requestCreated = requestService.createRequest(createRequest("help"), user.getId());
        ItemDto item = itemService.addNewItem(createItem(requestCreated.getId()), user.getId());

        RequestDtoWithItems requestFound = requestService.getRequestsByUser(user.getId()).getFirst();

        assertThat(requestFound.getDescription(), equalTo(requestCreated.getDescription()));
        assertThat(item.getId(), equalTo(requestFound.getItems().getFirst().getId()));
    }

    @Test
    public void getRequestByIdTest() {
        UserDto user = userService.createUser(createUser());
        RequestDtoWithItems requestCreated = requestService.createRequest(createRequest("i'm crying"), user.getId());
        ItemDto item = itemService.addNewItem(createItem(requestCreated.getId()), user.getId());

        RequestDtoWithItems requestFound = requestService.getRequestById(requestCreated.getId());

        assertThat(requestFound.getDescription(), equalTo(requestCreated.getDescription()));
        assertThat(item.getId(), equalTo(requestFound.getItems().getFirst().getId()));
    }

    @Test
    public void getAllRequestsTest() {
        UserDto user = userService.createUser(createUser());

        RequestDtoWithItems requestCreated1 = requestService.createRequest(createRequest("i'm crying"), user.getId());
        RequestDtoWithItems requestCreated2 = requestService.createRequest(createRequest("one more request"), user.getId());

        List<RequestDto> requestsFound = requestService.getAllRequests();

        assertThat(requestCreated1.getId(), equalTo(requestsFound.getFirst().getId()));
        assertThat(requestCreated2.getId(), equalTo(requestsFound.getLast().getId()));
        assertThat(requestsFound.size(), equalTo(2));
    }

    private CreateRequestDto createRequest(String description) {
        CreateRequestDto request = new CreateRequestDto();
        request.setDescription(description);

        return request;
    }

    private CreateUserDto createUser() {
        CreateUserDto request = new CreateUserDto();
        request.setName("GGGGGG");
        request.setEmail("test@test.test");

        return request;
    }

    private CreateItemDto createItem(long requestId) {
        CreateItemDto request = new CreateItemDto();
        request.setName("name");
        request.setDescription("AAAAAAAAARGH");
        request.setAvailable(true);
        request.setRequestId(requestId);

        return request;
    }
}
