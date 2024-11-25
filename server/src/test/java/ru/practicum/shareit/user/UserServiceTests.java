package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserStorage userStorageMock;
    private UserService service;
    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        service = new UserServiceImpl(mapper, userStorageMock);
    }

    @Test
    public void createUserTest() {
        CreateUserDto request = new CreateUserDto();
        request.setName("Ekaterina Ivanova");
        request.setEmail("poPP@mail.test");
        User user = mapper.map(request, User.class);

        when(userStorageMock.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = service.createUser(request);

        assertThat(userDto, equalTo(mapper.map(user, UserDto.class)));

        verify(userStorageMock, times(1))
                .save(any(User.class));
    }

    @Test
    public void createUserWithNotUniqueEmailTest() {
        CreateUserDto request = new CreateUserDto();
        request.setName("Ekaterina Ivanova");
        request.setEmail("poPP@mail.test");
        User user = mapper.map(request, User.class);

        when(userStorageMock.findByEmailLike("poPP@mail.test").isPresent())
                .thenThrow(new ConflictException("Данный email уже занят другим пользователем"));

        assertThrows(ConflictException.class, () -> service.createUser(request));
    }

    @Test
    public void updateUserTest() {
        UpdateUserDto request = new UpdateUserDto();
        request.setName("Matthew McConaughey");
        request.setEmail("poPP@mail.test");
        User user = mapper.map(request, User.class);
        user.setId(1L);

        when(userStorageMock.findById(1L))
                .thenReturn(Optional.of(user));
        when(userStorageMock.findByEmailLikeAndIdNot(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        when(userStorageMock.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = service.updateUser(request, 1);

        assertThat(userDto, equalTo(mapper.map(user, UserDto.class)));

        verify(userStorageMock, times(1))
                .findById(anyLong());
        verify(userStorageMock, times(1))
                .findByEmailLikeAndIdNot(anyString(), anyLong());
        verify(userStorageMock, times(1))
                .save(any(User.class));

        verifyNoMoreInteractions(userStorageMock);
    }

    @Test
    public void updateUserWithNotUniqueEmailTest() {
        UpdateUserDto request = new UpdateUserDto();
        request.setName("Ekaterina Ivanova");
        request.setEmail("poPP@mail.test");

        User user = mapper.map(request, User.class);
        user.setId(2L);

        when(userStorageMock.findById(2L))
                .thenReturn(Optional.of(user));

        when(userStorageMock.findByEmailLikeAndIdNot("poPP@mail.test", 2L))
                .thenThrow(new ConflictException("Данный email уже занят другим пользователем"));

        assertThrows(ConflictException.class, () -> service.updateUser(request, 2L));
    }

    @Test
    public void getUserByIdTest() {
        UpdateUserDto request = new UpdateUserDto();
        request.setName("Julianne Moore");
        request.setEmail("iuoOOO324TGHhh@mail.test");

        User user = mapper.map(request, User.class);
        user.setId(1L);

        when(userStorageMock.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = service.getUserById(1L);

        assertThat(userDto, equalTo(mapper.map(user, UserDto.class)));

        verify(userStorageMock, times(1))
                .findById(1L);
    }

    @Test
    public void getAllUsersTest() {
        CreateUserDto request1 = new CreateUserDto();
        request1.setName("Julianne Moore");
        request1.setEmail("poPP@mail.test");
        User user1 = mapper.map(request1, User.class);

        CreateUserDto request2 = new CreateUserDto();
        request2.setName("Gillian Anderson");
        request2.setEmail("iuoOOO324TGHhh@mail.test");
        User user2 = mapper.map(request2, User.class);

        List<User> allUsers = List.of(user1, user2);

        when(userStorageMock.findAll())
                .thenReturn(allUsers);

        List<UserDto> allDtos = service.getAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            assertThat(allDtos.get(i), equalTo(mapper.map(allUsers.get(i), UserDto.class)));
        }

        verify(userStorageMock, times(1)).findAll();
    }

    @Test
    public void deleteUserTest() {
        when(userStorageMock.existsById(anyLong()))
                .thenReturn(true);

        service.deleteUser(1L);
        verify(userStorageMock, times(1)).deleteById(1L);
    }

}
