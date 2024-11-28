package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {

    @Mock
    UserStorage userStorageMock;
    @Mock
    ItemStorage itemStorageMock;
    @Mock
    ItemRequestStorage requestStorageMock;
    private ItemRequestService service;
    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        service = new ItemRequestServiceImpl(mapper,
                userStorageMock,
                itemStorageMock,
                requestStorageMock);
    }

    @Test
    public void createRequestTest() {
        User user = createUser(1L);
        CreateRequestDto request = createRequestDto("dfkjdkjf");

        when(userStorageMock.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestStorageMock.save(any(Request.class)))
                .thenReturn(mapper.map(request, Request.class));

        RequestDtoWithItems dto = service.createRequest(request, user.getId());

        assertEquals(request.getDescription(), dto.getDescription());
    }

    @Test
    public void createRequestWithNonexistentUserTest() {
        when(userStorageMock.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.createRequest(createRequestDto("kggkldfgkldjsl"), 1L));

        verify(userStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void getRequestsByUserWithNonexistentUserTest() {
        when(userStorageMock.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getRequestsByUser(1L));

        verify(userStorageMock, times(1))
                .existsById(anyLong());
        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void getRequestsByUserWithNoRequestSuggestedTest() {
        User user = createUser(1L);

        when(userStorageMock.existsById(user.getId()))
                .thenReturn(true);
        when(requestStorageMock.findByRequesterIdOrderByCreated(user.getId()))
                .thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> service.getRequestsByUser(user.getId()));

        verify(userStorageMock, times(1))
                .existsById(user.getId());
        verify(requestStorageMock, times(1))
                .findByRequesterIdOrderByCreated(user.getId());
        verifyNoMoreInteractions(userStorageMock, requestStorageMock);
    }

    @Test
    public void getRequestByNonexistentIdTest() {
        when(requestStorageMock.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getRequestById(1L));

        verify(requestStorageMock, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(requestStorageMock);
    }

    private User createUser(long id) {
        User user = new User();
        user.setName("hhhhhhh");
        user.setEmail("uuuu@uu.test");
        user.setId(id);

        return user;
    }

    private CreateRequestDto createRequestDto(String description) {
        CreateRequestDto request = new CreateRequestDto();
        request.setDescription(description);

        return request;
    }
}
