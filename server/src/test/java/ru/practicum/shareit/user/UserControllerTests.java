package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl service;

    @Test
    public void createUserTest() throws Exception {
        UserDto user = createUser(1L);

        when(service.createUser(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())));

        verify(service, times(1)).createUser(any());
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDto user = createUser(1L);

        when(service.updateUser(any(), anyLong()))
                .thenReturn(user);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())));

        verify(service, times(1)).updateUser(any(), anyLong());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        UserDto user = createUser(1L);

        when(service.getUserById(anyLong()))
                .thenReturn(user);

        mvc.perform((get("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())));

        verify(service, times(1)).getUserById(anyLong());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        UserDto user = createUser(1L);

        when(service.getAllUsers())
                .thenReturn(List.of(user));

        mvc.perform((get("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(user.getName())))
                .andExpect(jsonPath("$[0].email", Matchers.is(user.getEmail())));

        verify(service, times(1)).getAllUsers();
    }

    @Test
    public void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteUser(anyLong());
    }

    private UserDto createUser(long id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setName(id + "ww");
        user.setEmail(id + "@test.test");

        return user;
    }
}
